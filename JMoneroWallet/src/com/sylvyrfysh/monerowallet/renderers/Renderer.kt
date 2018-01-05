package com.sylvyrfysh.monerowallet.renderers

import com.sylvyrfysh.monerowallet.WState
import imgui.IO
import imgui.ImGui

interface Renderer {
	fun renderState(): WState?
	fun render(imgui: ImGui?, io: IO?)
}