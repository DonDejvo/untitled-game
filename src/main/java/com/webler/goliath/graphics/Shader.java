package com.webler.goliath.graphics;
import com.webler.goliath.eventsystem.EventManager;
import com.webler.goliath.exceptions.ResourceFormatException;
import com.webler.goliath.exceptions.ResourceNotFoundException;
import lombok.Getter;
import org.joml.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private static final Logger log = Logger.getLogger(Shader.class.getName());
    @Getter
    private int program;
    private final String vertexSource;
    private final String fragmentSource;
    private final Set<Uniform> uniforms;

    public Shader(String vertexSource, String fragmentSource) {
        this.vertexSource = vertexSource;
        this.fragmentSource = fragmentSource;
        uniforms = new HashSet<>();
    }

    public void linkShader() {
        int vertexShader = compileShader(vertexSource, GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragmentSource, GL_FRAGMENT_SHADER);
        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        if(glGetProgrami(program, GL_LINK_STATUS) == 0) {
            log.severe(glGetProgramInfoLog(program));
        }

        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        initUniforms(vertexSource);
        initUniforms(fragmentSource);
    }

    public void initUniforms(String source) {
        glUseProgram(program);
        Pattern pattern = Pattern.compile("(uniform)( )+([a-zA-Z0-9]+)( )+([a-zA-Z0-9_]+)");
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            String group = matcher.group();
            String[] splitString = group.split("( )+");
            if (splitString.length > 2) {
                int loc = glGetUniformLocation(program, splitString[2]);
                uniforms.add(new Uniform(splitString[2], loc, splitString[1]));
            }
        }
    }

    public <T> void supplyUniform(String name, T value) {
        Uniform uniform = uniforms.stream()
                .filter((Uniform u) -> u.name().equals(name))
                .findAny()
                .orElse(null);

        if(uniform == null) {
            log.warning("Could not find uniform " + name);
            return;
        }

        int location = uniform.location();
        if(value instanceof Matrix4d) {
            float[] mat = new float[16];
            ((Matrix4d) value).get(mat);
            glUniformMatrix4fv(location, false, mat);
        } else if(value instanceof Vector3d vec) {
            glUniform3f(location, (float) vec.x, (float) vec.y, (float) vec.z);
        }  else if(value instanceof Vector4d vec) {
            glUniform4f(location, (float) vec.x, (float) vec.y, (float) vec.z, (float) vec.w);
        } else if(value instanceof Vector3d[] vec) {
            float[] buffer = new float[vec.length * 3];
            for(int i = 0; i < vec.length; i++) {
                buffer[i * 3] = (float) vec[i].x;
                buffer[i * 3 + 1] = (float) vec[i].y;
                buffer[i * 3 + 2] = (float) vec[i].z;
            }
            glUniform3fv(location, buffer);
        } else if(value instanceof Integer number) {
            glUniform1i(location, number);
        } else if(value instanceof Double number) {
            glUniform1f(location, number.floatValue());
        }
    }


    private int compileShader(String source, int type) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            log.severe(glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public void bind() {
        glUseProgram(program);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public static Shader load(String resourceName, String preVertex, String preFragment) {
        String textSource;
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
            if(is == null) {
                throw new ResourceNotFoundException("Could not load resource path '" + resourceName + "'");
            }
            textSource = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Could not load resource path '" + resourceName + "'");
        }
        try {
            String[] splitString = textSource.split("(#type)( )+([a-zA-Z0-9]+)");

            int startIndex, endIndex;

            startIndex = textSource.indexOf("#type") + 6;
            endIndex = textSource.indexOf('\n', startIndex);
            String firstPattern = textSource.substring(startIndex, endIndex).trim();

            startIndex = textSource.indexOf("#type", endIndex) + 6;
            endIndex = textSource.indexOf('\n', startIndex);
            String secondPattern = textSource.substring(startIndex, endIndex).trim();

            StringBuilder vertexSource;
            StringBuilder fragmentSource;

            if (firstPattern.equals("vertex")) {
                vertexSource = new StringBuilder(splitString[1]);
            } else if (secondPattern.equals("vertex")) {
                vertexSource = new StringBuilder(splitString[2]);
            } else {
                throw new ResourceFormatException("Missing vertex shader source");
            }

            if (firstPattern.equals("fragment")) {
                fragmentSource = new StringBuilder(splitString[1]);
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = new StringBuilder(splitString[2]);
            } else {
                throw new ResourceFormatException("Missing fragment shader source");
            }

            String[] preVertexSplit = preVertex.split(",");
            String[] preFragmentSplit = preFragment.split(",");

            insertDefines(vertexSource, preVertexSplit);

            insertDefines(fragmentSource, preFragmentSplit);

            return new Shader(vertexSource.toString(), fragmentSource.toString());
        } catch (Exception e) {
            throw new ResourceFormatException("Wrong format of shader source '" + resourceName + "':\n" + e.getMessage());
        }
    }

    private static void insertDefines(StringBuilder source, String[] preSplit) {
        int firstLineEndIdx;
        String firstLine;
        firstLineEndIdx = source.indexOf("\n", source.indexOf("#version") + 9);
        firstLine = source.substring(0, firstLineEndIdx + 1);
        source.delete(0, firstLineEndIdx);

        for (String s : preSplit) {
            if(s.trim().isEmpty()) {
                continue;
            }
            source.insert(0, "#define " + s.trim() + " 1" + "\n");
        }

        source.insert(0, firstLine);
    }

    public void destroy() {
        glDeleteProgram(program);
    }
}
