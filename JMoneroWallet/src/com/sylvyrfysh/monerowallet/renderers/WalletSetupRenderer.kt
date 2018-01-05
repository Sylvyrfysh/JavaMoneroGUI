package com.sylvyrfysh.monerowallet.renderers

import com.sylvyrfysh.monerowallet.*
import com.sylvyrfysh.monerowallet.WalletHandler.WalletHandlerStatus.*
import imgui.*

import glm_.vec2.Vec2
import glm_.vec4.Vec4
import org.apache.logging.log4j.*

class WalletSetupRenderer : Renderer{
	
	val windowOpen = booleanArrayOf(true)
	
	private val logger = LogManager.getLogger()
	
	override fun renderState(): WState {
		return WState.WALLET_SETUP
	}

	override fun render(imgui: ImGui?, io: IO?) {
		if(imgui == null)
			return
		with(imgui) {
			setNextWindowPos(Vec2(0, 0), Cond.FirstUseEver, Vec2());
			setNextWindowSize(Vec2(1280, 720), Cond.FirstUseEver);
			if (begin("Wallet Setup", windowOpen, WindowFlags.NoMove.i or WindowFlags.NoResize.i or WindowFlags.NoCollapse.i)) {
				
				end()
			}
		}
	}
}