package com.webler.goliath.graphics;
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
    protected static final Logger logger = Logger.getLogger(Shader.class.getName());
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

    /**
    * Compiles and links the shaders. This is the method that should be called from the OpenGL thread
    */
    public void linkShader() {
        int vertexShader = compileShader(vertexSource, GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragmentSource, GL_FRAGMENT_SHADER);
        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        // Returns the link status of the program.
        if(glGetProgrami(program, GL_LINK_STATUS) == 0) {
            logger.severe(glGetProgramInfoLog(program));
        }

        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        initUniforms(vertexSource);
        initUniforms(fragmentSource);
    }

    /**
    * Initializes the uniforms by parsing the given source. Uniform names are separated by spaces and each part is of the form uniformName ( location )
    * 
    * @param source - the source to parse
    */
    public void initUniforms(String source) {
        glUseProgram(program);
        Pattern pattern = Pattern.compile("(uniform)( )+([a-zA-Z0-9]+)( )+([a-zA-Z0-9_]+)");
        Matcher matcher = pattern.matcher(source);
        // Find uniform locations in the shader.
        while (matcher.find()) {
            String group = matcher.group();
            String[] splitString = group.split("( )+");
            // Add uniform to uniforms list
            if (splitString.length > 2) {
                int loc = glGetUniformLocation(program, splitString[2]);
                uniforms.add(new Uniform(splitString[2], loc, splitString[1]));
            }
        }
    }

    /**
    * Supply a value to a uniform. This is useful for setting values that are shared between programs such as GLSL and OpenGL
    * 
    * @param name - The name of the uniform to supply
    * @param value - The value to supply to the uniform ( can be Matrix4d Vector3d
    */
    public <T> void supplyUniform(String name, T value) {
        Uniform uniform = uniforms.stream()
                .filter((Uniform u) -> u.name().equals(name))
                .findAny()
                .orElse(null);

        // Find a uniform. If no uniform is found a warning is logged.
        if(uniform == null) {
            logger.warning("Could not find uniform " + name);
            return;
        }

        int location = uniform.location();
        // Uniforms the value of the uniform distribution.
        if(value instanceof Matrix4d) {
            float[] mat = new float[16];
            ((Matrix4d) value).get(mat);
            glUniformMatrix4fv(location, false, mat);
        // Uniforms the value of the uniform distribution.
        } else if(value instanceof Vector3d vec) {
            glUniform3f(location, (float) vec.x, (float) vec.y, (float) vec.z);
        // Uniforms the value of the uniform distribution.
        }  else if(value instanceof Vector4d vec) {
            glUniform4f(location, (float) vec.x, (float) vec.y, (float) vec.z, (float) vec.w);
        // Uniforms the value of the uniform variable.
        } else if(value instanceof Vector3d[] vec) {
            float[] buffer = new float[vec.length * 3];
            // Set the vector to the buffer.
            for(int i = 0; i < vec.length; i++) {
                buffer[i * 3] = (float) vec[i].x;
                buffer[i * 3 + 1] = (float) vec[i].y;
                buffer[i * 3 + 2] = (float) vec[i].z;
            }
            glUniform3fv(location, buffer);
        // Uniform value of the value.
        } else if(value instanceof Integer number) {
            glUniform1i(location, number);
        // Uniform value of the current value.
        } else if(value instanceof Double number) {
            glUniform1f(location, number.floatValue());
        }
    }


    /**
    * Compiles a shader. This is the method that compiles and returns OpenGL shaders. The source is expected to be a well formed GLSL source code and the type is expected to be one of GL_VERTEX_SHADER GL_FRAGMENT_SHADER or GL_FRAGMENT_CUBE
    * 
    * @param source - the GLSL source code to compile
    * @param type - the type of shader to create e. g. GL_VERTEX_SHADER
    * 
    * @return the handle of the created shader or - 1 if an error occurred during compilation ( in which case the error message will be logged
    */
    private int compileShader(String source, int type) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        // Returns the shader information for the shader.
        if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            logger.severe(glGetShaderInfoLog(shader));
        }
        return shader;
    }

    /**
    * Binds this shader to the OpenGL program. This is called by #bind ( GLSLProgram
    */
    public void bind() {
        glUseProgram(program);
    }

    /**
    * Unbinds the OpenGL program. This is called when the program is no longer in use and should not be used
    */
    public void unbind() {
        glUseProgram(0);
    }

    /**
    * Loads a shader from a resource. The preVertex and preFragment parameters are used to pre - render the vertex and fragment shaders before and after the shader is rendered.
    * 
    * @param resourceName - the name of the resource to load.
    * @param preVertex - the vertex shader pre - rendered. Can be null.
    * @param preFragment - the fragment shader pre - rendered. Can be null.
    * 
    * @return the shader or null if the resource could not be loaded for some reason ( not all resources are available
    */
    public static Shader load(String resourceName, String preVertex, String preFragment) {
        String textSource;
        InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
        // Throws a ResourceNotFoundException if the resource is null.
        if(is == null) {
            throw new ResourceNotFoundException(resourceName);
        }
        try {
            textSource = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new ResourceFormatException(resourceName, "Could not read text file");
        }

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

        // The vertex shader source.
        if (firstPattern.equals("vertex")) {
            vertexSource = new StringBuilder(splitString[1]);
        // If the second pattern is vertex or vertex shader source.
        } else if (secondPattern.equals("vertex")) {
            vertexSource = new StringBuilder(splitString[2]);
        } else {
            throw new ResourceFormatException(resourceName, "Missing vertex shader source.");
        }

        // The fragment shader source.
        if (firstPattern.equals("fragment")) {
            fragmentSource = new StringBuilder(splitString[1]);
        // If the second pattern is fragment or fragment shader source.
        } else if (secondPattern.equals("fragment")) {
            fragmentSource = new StringBuilder(splitString[2]);
        } else {
            throw new ResourceFormatException(resourceName, "Missing fragment shader source.");
        }

        String[] preVertexSplit = preVertex.split(",");
        String[] preFragmentSplit = preFragment.split(",");

        insertDefines(resourceName, vertexSource, preVertexSplit);

        insertDefines(resourceName, fragmentSource, preFragmentSplit);

        return new Shader(vertexSource.toString(), fragmentSource.toString());
    }

    /**
    * Inserts #define statements into the source. This is used to insert a set of pre - split defines that need to be inserted before the shader is compiled.
    * 
    * @param resourceName - the name of the resource being compiled.
    * @param source - the source to insert the defines into. Must not be null.
    * @param preSplit - the pre - split defines to insert. May be null
    */
    private static void insertDefines(String resourceName, StringBuilder source, String[] preSplit) {
        try {
            int firstLineEndIdx;
            String firstLine;
            firstLineEndIdx = source.indexOf("\n", source.indexOf("#version") + 9);
            firstLine = source.substring(0, firstLineEndIdx + 1);
            source.delete(0, firstLineEndIdx);

            for (String s : preSplit) {
                // If the string is empty continue.
                if(s.trim().isEmpty()) {
                    continue;
                }
                source.insert(0, "#define " + s.trim() + " 1" + "\n");
            }

            source.insert(0, firstLine);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ResourceFormatException(resourceName, "Invalid define shader source.");
        }
    }

    /**
    * Destroys OpenGL resources associated with this shader. This method is called by GLSL when the shader is no longer needed
    */
    public void destroy() {
        glDeleteProgram(program);
    }
}
