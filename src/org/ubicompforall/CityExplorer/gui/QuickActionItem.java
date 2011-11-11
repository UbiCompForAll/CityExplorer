package org.ubicompforall.CityExplorer.gui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ubicompforall.CityExplorer.R;

/**
 * Class that handles the quick action items.
 * @author Jaroslav Rakhmatoullin
 *
 */
public class QuickActionItem extends LinearLayout implements Checkable {
    
	/**
	 * Field containing a boolean value that decides whether the item is checked or not.
	 */
	private boolean mChecked;

	/**
	 * Field containing the Checked state set.
	 */
    private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

    /**
     * Public constructor for creating a Quick Action item.
     * @param context The specified context.
     * @param attrs The specified set of attributes.
     */
    public QuickActionItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }
    
    /**
     * Set the icon of the quick action item.
     * @param drawable The Drawable you want the icon to be.
     */
    public void setImageDrawable(Drawable drawable) {
    	((ImageView)findViewById(R.id.quickaction_icon)).setImageDrawable(drawable);
    }
    
    /**
     * Set the label you wan the quick action item to have.
     * @param text The text you want to set.
     */
    public void setText(String text) {
    	((TextView)findViewById(R.id.quickaction_text)).setText(text);
    }
}