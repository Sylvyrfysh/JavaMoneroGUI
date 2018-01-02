package com.sylvyrfysh.monerowallet.renderers

import com.sylvyrfysh.monerowallet.*
import com.sylvyrfysh.monerowallet.Network.NetworkStatus.*
import imgui.*

import glm_.vec2.Vec2
import glm_.vec4.Vec4

class BeginRenderer : Renderer {
	val windowOpen = booleanArrayOf(true)
	val localNode = 0
	val remoteNode = 1
	private var selectedNode = intArrayOf(remoteNode)
	private var remoteNodeAddress = CharArray(256)
	private var remoteNodePort = CharArray(256)
	private var connectionTested = false
	
	init{
		System.arraycopy("node.moneroworld.com".toCharArray(),0,remoteNodeAddress,0,"node.moneroworld.com".toCharArray().size)
		System.arraycopy("18089".toCharArray(),0,remoteNodePort,0,"18089".toCharArray().size)
	}
	
	override fun renderState() : WState{
		return WState.FIRST_RUN
	}
	
	override fun render(imgui : ImGui, io : IO){
		with(imgui){
			setNextWindowPos(Vec2(0,0), Cond.FirstUseEver, Vec2());
			setNextWindowSize(Vec2(1280,720), Cond.FirstUseEver);
			if(begin("Setup", windowOpen, WindowFlags.NoMove.i or WindowFlags.NoResize.i or WindowFlags.NoCollapse.i)) {
				
				text("Node Type:")
				radioButton("Local Node", selectedNode, 0);
				if(isItemHovered(0))
					setTooltip("Running a local node will contribute to the decentralization of the network, but takes up a lot of space on your computer.");
				sameLine(0);
				radioButton("Remote Node", selectedNode, 1);
				if(isItemHovered(0))
					setTooltip("Using a remote node will have your computer connect to it and download necessary information for your wallet. This is less secure.");
				textColored(Vec4(0,1,0,1),"Node type and address can be changed later in settings.");
				
				separator()
				if(selectedNode[0] == remoteNode){
					text("Remote Node Address")
					inputText("",remoteNodeAddress)
					text("Remote Node Port")
					inputText("",remoteNodePort)
					if(button("Test Connection")){
						Network.tryConnect(remoteNodeAddress,remoteNodePort);
						connectionTested = true
					}
					if(connectionTested){
						sameLine(0)
						textColored(when(Network.getStatus()){
							ERROR -> Vec4(1,0,0,1)
							UNCONNECTED -> Vec4(.8)
							CONNECTED -> Vec4(0,1,0,1)
							else -> Vec4(1)
						},Network.getStatusMessage())
					}
				}
				
				end();
			}
		}
	}
}