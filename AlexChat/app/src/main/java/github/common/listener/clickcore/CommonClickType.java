package github.common.listener.clickcore;
public enum CommonClickType {
	/**返回*/
	back,
	/**下一步，并且结束自己*/
	cls2Killed,
	/**下一步，不结束自己*/
	cls0Killed,
	/**带意图的，并且结束自己*/
	intent2Killed,
	/**带意图的，不结束自己*/
	intent0Killed
}