package com.sylvyrfysh.monerowallet.renderers

import com.sylvyrfysh.monerowallet.*
import com.sylvyrfysh.monerowallet.WalletHandler.WalletHandlerStatus.*
import imgui.*

import java.io.*

import glm_.vec2.Vec2
import glm_.vec4.Vec4
import org.apache.logging.log4j.*

class WalletSetupRenderer : Renderer{
	
	val windowOpen = booleanArrayOf(true)
	
	private val logger = LogManager.getLogger()
	
	private var walletName = CharArray(256)
	private var walletPassword = CharArray(256)
	
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
				inputText("Wallet Name", walletName, InputTextFlags.EnterReturnsTrue.i)
				inputText("Wallet Password", walletPassword, InputTextFlags.EnterReturnsTrue.i)
				if(button("I have a wallet")){
					val pb = ProcessBuilder("res/v0_11_1_0/monero-wallet-cli.exe")
					pb.redirectErrorStream(true)
					val ps = pb.start()
					val isr = InputStreamReader(ps.inputStream)
					val br = BufferedReader(isr)
					var line = ""
					while (line != null){
						logger.trace("CLIld: {}",line)
						if(line.contains("If the wallet doesn't exist, it will be created.")){
							ps.outputStream.write(ImGuiUtils.imguiToStr(walletName).toByteArray())
							ps.outputStream.write('\n'.toInt())
							ps.outputStream.flush()
						}else if(line.contains("Wallet and key files found, loading...")){
							ps.outputStream.write(ImGuiUtils.imguiToStr(walletPassword).toByteArray())
							ps.outputStream.write('\n'.toInt())
							ps.outputStream.flush()
						}else if(line.contains("Opened wallet:")){
							ps.destroyForcibly();
							MoneroWalletMain.renderState = WState.WALLET_OPEN
							WalletHandler.openRPC(ImGuiUtils.imguiToStr(walletName),ImGuiUtils.imguiToStr(walletPassword))
							end()
							return
						}
						line = br.readLine()
					}
				}
				if(button("Create a wallet")){
					val pb = ProcessBuilder("res/v0_11_1_0/monero-wallet-cli.exe")
					pb.redirectErrorStream(true)
					val ps = pb.start()
					val isr = InputStreamReader(ps.inputStream)
					val br = BufferedReader(isr)
					var line = ""
					while (line != null){
						logger.trace("CLIcr: {}",line)
						if(line.contains("If the wallet doesn't exist, it will be created.")){
							ps.outputStream.write(ImGuiUtils.imguiToStr(walletName).toByteArray())
							ps.outputStream.write('\n'.toInt())
							ps.outputStream.flush()
						}else if(line.contains("Confirm creation of new wallet named")){
							ps.outputStream.write("y".toByteArray())
							ps.outputStream.write('\n'.toInt())
							ps.outputStream.flush()
							ps.outputStream.write(ImGuiUtils.imguiToStr(walletPassword).toByteArray())
							ps.outputStream.write('\n'.toInt())
							ps.outputStream.flush()
							ps.outputStream.write(ImGuiUtils.imguiToStr(walletPassword).toByteArray())
							ps.outputStream.write('\n'.toInt())
							ps.outputStream.flush()
						}else if(line.contains("List of available languages for your wallet's seed:")){
							ps.outputStream.write('1'.toInt())
							ps.outputStream.write('\n'.toInt())
							ps.outputStream.flush()
						}
						line = br.readLine()
					}
				}
				end()
			}
		}
	}
}