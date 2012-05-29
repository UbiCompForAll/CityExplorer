/**
 * @contributor(s): Jaroslav Rakhmatoullin (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
 * @revised:
 *
 * Copyright (C) 2011-2012 UbiCompForAll Consortium (SINTEF, NTNU)
 * for the UbiCompForAll project
 *
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 */

/**
 * @description:
 * This class handles the Quick Action Popup windows.
 * 
 */

package org.ubicompforall.CityExplorer.gui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;

public class QuickActionPopup extends PopupWindow implements KeyEvent.Callback {

	/**
	 * Field containing the context.
	 */
	private final Context mContext;

	/**
	 * Field containing the layout inflater.
	 */
	private final LayoutInflater mInflater;

	/**
	 * Field containing the window manager.
	 */
	private final WindowManager mWindowManager;

	/**
	 * Field containing the view of the content.
	 */
	private View contentView;

	/**
	 * Field containing the width of the screen.
	 */
	private int mScreenWidth;

	/**
	 * Field containing the horizontal shadow on the screen.
	 */
	private int mShadowHoriz;

	/**
	 * Field containing an ImageView of the up arrow.
	 */
	private ImageView mArrowUp;

	/**
	 * Field containing an ImageView of the down arrow.
	 */
	private ImageView mArrowDown;

	/**
	 * Field containing the track ViewGroup.
	 */
	private ViewGroup mTrack;

	/**
	 * Field containing the PView.
	 */
	private View mPView;

	/**
	 * Field containing the Anchor
	 */
	private Rect mAnchor;

	/**
	 * Public constructor for creating a new quick action popup window.
	 * @param context The desired context.
	 * @param pView The PView.
	 * @param rect The Rect object.
	 */
	public QuickActionPopup(Context context, View pView, Rect rect) {
		super(context);

		mPView = pView;
		mAnchor = rect;

		mContext = context;
		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		mInflater = ((Activity)mContext).getLayoutInflater();

		setContentView(R.layout.quickaction_poi);

		mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();

		setWindowLayoutMode(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		setWidth(mScreenWidth + mShadowHoriz + mShadowHoriz);

		debug(2, "setWidth("+mScreenWidth +"+"+ mShadowHoriz +"+"+ mShadowHoriz+");");
		//debug(0, "pView is "+pView+" and Rect is "+rect );

		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

		setBackgroundDrawable(new ColorDrawable(0));

		mArrowUp = (ImageView) contentView.findViewById(R.id.arrow_up);
		mArrowDown = (ImageView) contentView.findViewById(R.id.arrow_down);

		mTrack = (ViewGroup) contentView.findViewById(R.id.quickaction);

		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
	} // CONSTRUCTOR

	
	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug




	/**
	 * Sets the current content view.
	 * @param resId ID for an XML layout resource to load (e.g., R.layout.main_page)
	 */
	private void setContentView(int resId) {
		contentView = mInflater.inflate(resId, null);
		super.setContentView(contentView);
	}

	/**
	 * Gets the header view.
	 * @return The View of the wanted header.
	 */
	public View getHeaderView() {
		return contentView.findViewById(R.id.quickaction_header);
	}

	/**
	 * Sets the title of the quick action popup window.
	 * @param title The title you want the quick action popup to have.
	 */
	public void setTitle(CharSequence title) {
		contentView.findViewById(R.id.quickaction_header_content).setVisibility(View.VISIBLE);
		contentView.findViewById(R.id.quickaction_primary_text).setVisibility(View.VISIBLE);
		((TextView) contentView.findViewById(R.id.quickaction_primary_text)).setText(title);
	}

	/**
	 * Sets the title of the quick action popup window.
	 * @param resid ID for an XML identifier resource to load (e.g., R.id.title)
	 */
	public void setTitle(int resid) {
		setTitle(mContext.getResources().getString(resid));
	}

	/**
	 * Sets the text of the quick action popup window.
	 * @param text The text you want the quick action popup to have.
	 */
	public void setText(CharSequence text) {
		contentView.findViewById(R.id.quickaction_header_content).setVisibility(View.VISIBLE);
		contentView.findViewById(R.id.quickaction_secondary_text).setVisibility(View.VISIBLE);
		((TextView) contentView.findViewById(R.id.quickaction_secondary_text)).setText(text);
	}

	/**
	 * Sets the text of the quick action popup window.
	 * @param resid ID for an XML identifier resource to load (e.g., R.id.text)
	 */
	public void setText(int resid) {
		setText(mContext.getResources().getString(resid));
	}

	/**
	 * Sets the icon for the quick action item.
	 * @param url A string containing an URL to the icon.
	 */
	public void setIcon(String url) {
		contentView.findViewById(R.id.quickaction_icon).setVisibility(View.VISIBLE);        
	}

	/**
	 * Sets the icon for the quick action item.
	 * @param bm The Bitmap of the wanted icon.
	 */
	public void setIcon(Bitmap bm) {
		contentView.findViewById(R.id.quickaction_icon).setVisibility(View.VISIBLE);
		final ImageView vImage = (ImageView) contentView.findViewById(R.id.quickaction_icon);
		vImage.setImageBitmap(bm);
	}

	/**
	 * Sets the icon for the quick action item.
	 * @param d The Drawable of the wanted icon.
	 */
	public void setIcon(Drawable d) {
		contentView.findViewById(R.id.quickaction_icon).setVisibility(View.VISIBLE);
		final ImageView vImage = (ImageView) contentView.findViewById(R.id.quickaction_icon);
		vImage.setImageDrawable(d);
	}

	/**
	 * Sets the icon for the quick action item.
	 * @param resid ID for an XML icon resource to load (e.g., R.layout.icon)
	 */
	public void setIcon(int resid) {
		setIcon(mContext.getResources().getDrawable(resid));
	}

	/**
	 * Show the correct call-out arrow based on a {@link R.id} reference.
	 * @param whichArrow Which arrow to show.
	 * @param requestedX The requested X value/coordinate.
	 */
	private void showArrow(int whichArrow, int requestedX) {
		final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
		final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

		// Bad to get width, might cause memory leak
		final int arrowWidth = mContext.getResources().getDrawable(R.drawable.quickaction_arrow_up).getIntrinsicWidth();

		showArrow.setVisibility(View.VISIBLE);
		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
		param.leftMargin = requestedX - arrowWidth / 2;

		hideArrow.setVisibility(View.INVISIBLE);
	}

	/**
	 * Adds an item to the quick action popup.
	 * @param drawable The drawable object you want to set as the icon.
	 * @param text The text you want the item to have.
	 * @param l The listener you want it to have.
	 */
	public void addItem(Drawable drawable, String text, OnClickListener l) {
		QuickActionItem view = (QuickActionItem) mInflater.inflate(R.layout.quickaction_item, mTrack, false);
		view.setChecked(false);
		view.setImageDrawable(drawable);
		view.setText(text);
		view.setOnClickListener(l);

		final int index = mTrack.getChildCount() - 1;
		mTrack.addView(view, index);
	}//addItem

	/**
	 * Adds an item to the quick action popup.
	 * @param drawable The drawable int you want to set as the icon.
	 * @param text The text you want the item to have.
	 * @param l The listener you want it to have.
	 */
	public void addItem(int drawable, String text, OnClickListener l) {
		addItem(mContext.getResources().getDrawable(drawable), text, l);
	}

	/**
	 * Adds an item to the quick action popup.
	 * @param drawable The drawable object you want to set as the icon.
	 * @param resid The resource identifier with the text you want the item to have.
	 * @param l The listener you want it to have.
	 */
	public void addItem(Drawable drawable, int resid, OnClickListener l) {
		addItem(drawable, mContext.getResources().getString(resid), l);
	}

	/**
	 * Adds an item to the quick action popup.
	 * @param drawable The drawable int you want to set as the icon.
	 * @param resid The resource identifier with the text you want the item to have.
	 * @param l The listener you want it to have.
	 */
	public void addItem(int drawable, int resid, OnClickListener l) {
		addItem(mContext.getResources().getDrawable(drawable), mContext.getResources().getText(resid).toString(), l);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}
		return false;
	}// onKeyUp

	/**
	 * Method that closes the quick action popup window when the back button is pressed.
	 */
	private void onBackPressed() {
		dismiss();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
		return false;
	}

	/**
	 * Method that shows the quick action popup window.
	 */
	public void show() {
		show(mAnchor.centerX());
	}

	/**
	 * Method that shows the quick action popup window.
	 * @param requestedX The wanted X value/coordinate to position the popup from. 
	 */
	public void show(int requestedX) {
		super.showAtLocation(mPView, Gravity.NO_GRAVITY, 0, 0);

		// Calculate properly to position the popup the correctly based on height of popup
		if (isShowing()) {
			int x, y;
			this.getContentView().measure(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			final int blockHeight = this.getContentView().getMeasuredHeight();

			x = -mShadowHoriz;

			if (mAnchor.top > blockHeight) {
				// Show downwards callout when enough room, aligning bottom block
				// edge with top of anchor area, and adjusting to inset arrow.
				showArrow(R.id.arrow_down, requestedX);
				y = mAnchor.top - blockHeight;

			} else {
				// Otherwise show upwards callout, aligning block top with bottom of
				// anchor area, and adjusting to inset arrow.
				showArrow(R.id.arrow_up, requestedX);
				y = mAnchor.bottom;
			}
			this.update(x, y, -1, -1);
		}
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return false;
	}//onKeyLongPress

}//class QuickActionPopup
