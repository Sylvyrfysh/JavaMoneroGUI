package com.sylvyrfysh.monerowallet

class ImGuiUtils {
	companion object {
		public fun imguiToStr(arr: CharArray): String {
			var lastNonNullChar = 0
			for (cs in arr) {
				if (cs == 0.toChar())
					break;
				lastNonNullChar++;
			}
			return String(arr, 0, lastNonNullChar);
		}
	}
}