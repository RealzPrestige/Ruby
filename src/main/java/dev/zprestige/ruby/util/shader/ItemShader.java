package dev.zprestige.ruby.util.shader;

import dev.zprestige.ruby.Ruby;
import org.lwjgl.opengl.GL20;

public class ItemShader extends FramebufferShader {
    public static ItemShader Instance = new ItemShader();
    public float mix = 0.0f;
    public float alpha = 1.0f;

    public ItemShader() {
        super("itemglow.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("texture");
        setupUniform("texelSize");
        setupUniform("color");
        setupUniform("divider");
        setupUniform("radius");
        setupUniform("maxSample");
        setupUniform("dimensions");
        setupUniform("mixFactor");
        setupUniform("minAlpha");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0);
        GL20.glUniform2f(getUniform("texelSize"), 1F / Ruby.mc.displayWidth * (radius * quality), 1F / Ruby.mc.displayHeight * (radius * quality));
        GL20.glUniform3f(getUniform("color"), red, green, blue);
        GL20.glUniform1f(getUniform("divider"), 140F);
        GL20.glUniform1f(getUniform("radius"), radius);
        GL20.glUniform1f(getUniform("maxSample"), 10F);
        GL20.glUniform2f(getUniform("dimensions"), Ruby.mc.displayWidth, Ruby.mc.displayHeight);
        GL20.glUniform1f(getUniform("mixFactor"), mix);
        GL20.glUniform1f(getUniform("minAlpha"), alpha);
    }

}
