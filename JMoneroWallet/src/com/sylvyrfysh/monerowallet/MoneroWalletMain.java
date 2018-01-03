package com.sylvyrfysh.monerowallet;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import com.sylvyrfysh.monerowallet.renderers.BeginRenderer;
import com.sylvyrfysh.monerowallet.renderers.Renderer;

import glm_.vec4.Vec4;
import imgui.IO;
import imgui.ImGui;
import imgui.impl.LwjglGL3;
import uno.glfw.GlfwWindow;

public class MoneroWalletMain {

	public static void main(String[] args) {
		new MoneroWalletMain().run();
	}
	private List<Renderer> renderers;
	private GlfwWindow window;
	private uno.glfw.glfw glfw = uno.glfw.glfw.INSTANCE;
	private uno.glfw.windowHint windowHint = uno.glfw.windowHint.INSTANCE;
	private LwjglGL3 lwjglGL3 = LwjglGL3.INSTANCE;
	private static ImGui imgui = ImGui.INSTANCE;
	private IO io = IO.INSTANCE;

	public void run() {

		init();
		
		// Setup ImGui binding
		lwjglGL3.init(window, true);
		
		FontConfig fc=new FontConfig();
		
		Font f=io.getFonts().addFontFromFileTTF("extraFonts/DroidSans.ttf", 16.0f, fc, new int[] {});
		io.setFontDefault(f);
		
		while (window.getOpen())
			loop();

		lwjglGL3.shutdown();

		window.destroy();
		glfw.terminate();
	}

	private void init() {

		glfw.init();
		windowHint.getContext().setVersion("3.3");
		windowHint.setProfile("core");

		window = new GlfwWindow(1280, 720, "Monero Java Wallet");

		window.makeContextCurrent();
		glfw.setSwapInterval(1); // Enable vsync
		window.show();
		
		renderers=new ArrayList<>();
		renderers.add(new BeginRenderer());

		GL.createCapabilities();
	}

	private void loop() {
		
		glfw.pollEvents();
		lwjglGL3.newFrame();

		gln.GlnKt.glViewport(window.getFramebufferSize());
		gln.GlnKt.glClearColor(new Vec4(.2));
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		renderers.get(0).render(imgui, io);
		
		imgui.render();
		window.swapBuffers();

		gln.GlnKt.checkError("loop", true);
	}
}
