package com.webler.goliath.graphics;

import lombok.Getter;

import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {
    @Getter
    private final int fbo;
    private final int rbo;
    private Texture tex;
    @Getter
    private int width;
    @Getter
    private int height;

    public Framebuffer(int width, int height) {
        this.width = width;
        this.height = height;
        tex = new Texture(1920, 1080);

        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex.getTexId(), 0);

        rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width,height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
    * Destroys OpenGL resources associated with this Framebuffer and its textures. This is called by GL_DETACH
    */
    public void destroy() {
        glDeleteFramebuffers(fbo);
        glDeleteRenderbuffers(rbo);
        glDeleteTextures(tex.getTexId());
    }

    /**
    * Returns the texture ID associated with this Texture. Note that this will return 0 if there is no texture associated with this Texture.
    * 
    * 
    * @return the texture ID associated with this Texture or - 1 if there is no texture associated with this Texture or if the texture is
    */
    public int getTexId() {
        return tex.getTexId();
    }

    /**
    * Sets the size of this Framebuffer. This is called by OpenGL when the size of this Framebuffer changes.
    * 
    * @param width - The new width of this Framebuffer. Must be greater than 0.
    * @param height - The new height of this Framebuffer. Must be greater than 0
    */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;

        tex.destroy();

        tex = new Texture(width, height);

        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex.getTexId(), 0);

        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width,height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
    * Blits the framebuffer to the screen. This is useful for drawing a portion of the screen on a per - frame basis.
    * 
    * @param dstWidth - the width of the framebuffer to be blitted to. It must be greater than or equal to 0 and less than or equal to #getWidth ().
    * @param dstHeight - the height of the framebuffer to be blitted to
    */
    public void blitFramebuffer(int dstWidth, int dstHeight) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBlitFramebuffer(0, 0, width, height, 0, 0, dstWidth, dstHeight,
                GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
    }
}
