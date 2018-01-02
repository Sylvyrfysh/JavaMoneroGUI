package com.sylvyrfysh.monerowallet.renderers;

import com.sylvyrfysh.monerowallet.WState;

import imgui.IO;
import imgui.ImGui;

public interface Renderer {
	public WState renderState();
	public void render(ImGui imgui,IO io);
}
