package com.sylvyrfysh.monerowallet.renderers

import com.sylvyrfysh.monerowallet.*
import com.sylvyrfysh.monerowallet.WalletHandler.WalletHandlerStatus.*
import imgui.*

import glm_.vec2.Vec2
import glm_.vec4.Vec4
import org.apache.logging.log4j.*

class NodePickRenderer : Renderer {
	val windowOpen = booleanArrayOf(true)
	val localNode = 0
	val remoteNode = 1
	private var selectedNode = intArrayOf(remoteNode)
	private var remoteNodeAddress = CharArray(256)
	private var remoteNodePort = CharArray(256)
	private var connectionTested = false

	private val logger = LogManager.getLogger()

	init {
		System.arraycopy("node.moneroworld.com".toCharArray(), 0, remoteNodeAddress, 0, "node.moneroworld.com".toCharArray().size)
		System.arraycopy("18089".toCharArray(), 0, remoteNodePort, 0, "18089".toCharArray().size)
	}

	override fun renderState(): WState {
		return WState.SELECT_NODE
	}

	override fun render(imgui: ImGui?, io: IO?) {
		if (imgui == null)
			return
		with(imgui) {
			setNextWindowPos(Vec2(0, 0), Cond.FirstUseEver, Vec2());
			setNextWindowSize(Vec2(1280, 720), Cond.FirstUseEver);
			if (begin("Setup", windowOpen, WindowFlags.NoMove.i or WindowFlags.NoResize.i or WindowFlags.NoCollapse.i)) {

				text("Node Type:")
				radioButton("Local Node", selectedNode, 0);
				if (isItemHovered(0))
					setTooltip("Running a local node will contribute to the decentralization of the network, but takes up a lot of space on your computer.");
				sameLine(0);
				radioButton("Remote Node", selectedNode, 1);
				if (isItemHovered(0))
					setTooltip("Using a remote node will have your computer connect to it and download necessary information for your wallet. This is less secure.");
				textColored(Vec4(0, 1, 0, 1), "Node type and address can be changed later in settings.");

				separator()
				if (selectedNode[0] == remoteNode) {
					text("Remote Node Address")
					inputText("", remoteNodeAddress)
					text("Remote Node Port")
					if (inputText("", remoteNodePort, InputTextFlags.EnterReturnsTrue.i) or button("Test Connection")) {
						WalletHandler.clearErrorStatus();
						var tcThread = Thread(Runnable() { WalletHandler.tryConnect(ImGuiUtils.imguiToStr(remoteNodeAddress), ImGuiUtils.imguiToStr(remoteNodePort)) });
						tcThread.name = "TestConn"
						tcThread.start()
						connectionTested = true
					}
					if (connectionTested) {
						sameLine(0)
						textColored(when (WalletHandler.getStatus()) {
							ERROR -> Vec4(1, 0, 0, 1)
							UNCONNECTED -> Vec4(.8)
							CONNECTED -> Vec4(0, 1, 0, 1)
							else -> Vec4(1)
						}, WalletHandler.getStatusMessage())
					}
					if (WalletHandler.hasValidNode()) {
						if (button("Continue")) {
							WalletHandler.commitNode()
							MoneroWalletMain.renderState = WState.WALLET_SETUP
						}
					}
				}

				end();
			}
		}
	}
}