package com.webler.goliath.graphics;

import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {
    private final int fbo;
    private final int rbo;
    private Texture tex;
    private int width;
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

    public void destroy() {
        glDeleteFramebuffers(fbo);
        glDeleteRenderbuffers(rbo);
        glDeleteTextures(tex.getTexId());
    }

    public int getFbo() {
        return fbo;
    }

    public int getTexId() {
        return tex.getTexId();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

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

    public void blitFramebuffer(int dstWidth, int dstHeight) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBlitFramebuffer(0, 0, width, height, 0, 0, dstWidth, dstHeight,
                GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
    }
}
