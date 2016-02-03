package github.common.listener;
/**监听 IndexActivity 中切换Fragment的 顺序，在 Fragment中写：
 * <pre>
 * public void onAttach(Activity activity)
 * {
 * 	super.onAttach(activity);
 * 	((IndexActivity)activity).setOnFragmentSelectedListener(new onFragmentSelectedListener());
 * }
 * </pre>*/
public interface OnFragmentSelectedListener{
	/**被选中的 Fragment的下标*/
	public void onFragmentSelected(int indexSelected);
}
