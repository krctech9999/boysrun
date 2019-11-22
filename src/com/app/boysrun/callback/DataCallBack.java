package com.app.boysrun.callback;

public interface DataCallBack {
	
	public void updateDisplayValues(int steps_val);
	
	public void onCharacteristicWrite();
	
	public void changeUpdateval(boolean b);
	
	public void startVlcVideo();
	
	/**
	 * @param rawId 要播的音樂raw id
	 */
	public void initMusic(final int rawId);
	
	/**
	 * 重設game模式
	 */
	public void resetGame();
	
	public void finishDataActivity();
	
	public void gotoActivity(final Class<?> cls);

	public void gotoResultDialog();
	
}
