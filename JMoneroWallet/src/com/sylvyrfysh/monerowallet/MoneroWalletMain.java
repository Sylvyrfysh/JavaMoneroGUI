package com.sylvyrfysh.monerowallet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import com.sylvyrfysh.monerowallet.renderers.NodePickRenderer;
import com.sylvyrfysh.monerowallet.renderers.Renderer;
import com.sylvyrfysh.monerowallet.renderers.WalletSetupRenderer;

import glm_.vec4.Vec4;
import imgui.Font;
import imgui.FontConfig;
import imgui.IO;
import imgui.ImGui;
import imgui.impl.LwjglGL3;
import uno.glfw.GlfwWindow;

public class MoneroWalletMain {

	private static final Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		try {
			System.setOut(new PrintStream(new OutputStream() {
				private static final int BUF_SIZE = 2048;
				private byte[] buffer = new byte[BUF_SIZE];
				int count = 0;

				@Override
				public void write(int b) throws IOException {
					if (b == 0) {
						return;
					} else if (b == '\n') {
						logger.info(new String(buffer, 0, count).trim());
						count = 0;
						buffer = new byte[BUF_SIZE];
						return;
					}
					// would this be writing past the buffer?
					if (count == buffer.length) {
						// grow the buffer
						final int newBufLength = buffer.length + BUF_SIZE;
						final byte[] newBuf = new byte[newBufLength];
						System.arraycopy(buffer, 0, newBuf, 0, buffer.length);
						buffer = newBuf;
					}

					buffer[count] = (byte) b;
					count++;
				}
			}));

			System.setErr(new PrintStream(new OutputStream() {
				private static final int BUF_SIZE = 2048;
				private byte[] buffer = new byte[BUF_SIZE];
				int count = 0;

				@Override
				public void write(int b) throws IOException {
					if (b == 0) {
						return;
					} else if (b == '\n') {
						logger.error(new String(buffer, 0, count).trim());
						count = 0;
						buffer = new byte[BUF_SIZE];
						return;
					}
					// would this be writing past the buffer?
					if (count == buffer.length) {
						// grow the buffer
						final int newBufLength = buffer.length + BUF_SIZE;
						final byte[] newBuf = new byte[newBufLength];
						System.arraycopy(buffer, 0, newBuf, 0, buffer.length);
						buffer = newBuf;
					}

					buffer[count] = (byte) b;
					count++;
				}
			}));

			Options options = new Options();

			Option input = new Option("tv", "tools-version", true,
					String.format("Monero tools version. Default %s", WalletHandler.TOOLS_VERSION));
			input.setRequired(false);
			options.addOption(input);

			CommandLineParser parser = new DefaultParser();
			HelpFormatter formatter = new HelpFormatter();
			CommandLine cmd;

			try {
				cmd = parser.parse(options, args);
			} catch (ParseException e) {
				System.out.println(e.getMessage());
				formatter.printHelp("utility-name", options);

				System.exit(1);
				return;
			}

			if (cmd.hasOption("tools-version")) {
				WalletHandler.TOOLS_VERSION = cmd.getOptionValue("tools-version");
			}

			new MoneroWalletMain().run();
		} catch (Exception e) {
			logger.fatal(
					"An error occured. Please submit to https://github.com/Sylvyrfysh/JavaMoneroGUI/issues with details on what you were doing.",
					e);
		}
	}

	private List<Renderer> renderers;
	private GlfwWindow window;
	private uno.glfw.glfw glfw = uno.glfw.glfw.INSTANCE;
	private uno.glfw.windowHint windowHint = uno.glfw.windowHint.INSTANCE;
	private LwjglGL3 lwjglGL3 = LwjglGL3.INSTANCE;
	private static ImGui imgui = ImGui.INSTANCE;
	private IO io = IO.INSTANCE;
	public static WState renderState = WState.SELECT_NODE;

	public void run() {

		init();

		// Setup ImGui binding
		lwjglGL3.init(window, true);

		FontConfig fc = new FontConfig();

		Font f = io.getFonts().addFontFromFileTTF("extraFonts/DroidSans.ttf", 16.0f, fc, new int[] {});
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

		renderers = new ArrayList<>();
		renderers.add(new NodePickRenderer());
		renderers.add(new WalletSetupRenderer());
		
		GL.createCapabilities();
	}

	private void loop() {

		glfw.pollEvents();
		lwjglGL3.newFrame();

		gln.GlnKt.glViewport(window.getFramebufferSize());
		gln.GlnKt.glClearColor(new Vec4(.2));
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		for(Renderer r:renderers) {
			if(r.renderState() == renderState )
				r.render(imgui, io);
		}

		imgui.render();
		window.swapBuffers();

		gln.GlnKt.checkError("loop", true);
	}
}
