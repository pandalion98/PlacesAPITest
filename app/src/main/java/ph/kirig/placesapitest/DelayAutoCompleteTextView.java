/**
 * Adapted from various sources. I forgot where I got this from.
 * ctto I guess.
 */

package ph.kirig.placesapitest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;

/**
 * Created by Gene on 15 Oct 2018
 * Kirig Technologies
 * gene(at)kirig.ph
 */

public class DelayAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;

    private int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;
    private ProgressBar mLoadingIndicator;

    @SuppressLint("HandlerLeak") // Cleaned up in onDetachedFromWindow
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DelayAutoCompleteTextView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
        }
    };

    public DelayAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets a progress bar that gets manipulated by this view.
     * i.e. Show if DelayAutoCompleteTextView is busy getting results.
     *
     * @param progressBar ProgressBar to manipulate
     */
    public void setLoadingIndicator(ProgressBar progressBar) {
        mLoadingIndicator = progressBar;
    }

    /**
     * Sets how many milliseconds before view starts autocompleting
     *
     * @param autoCompleteDelayMs Autocomplete delay in ms
     */
    public void setAutoCompleteDelay(int autoCompleteDelayMs) {
        mAutoCompleteDelay = autoCompleteDelayMs;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
    }

    @Override
    public void onFilterComplete(int count) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.GONE);
        }
        super.onFilterComplete(count);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing()) {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputManager.hideSoftInputFromWindow(findFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS)) {
                return true;
            }
        }

        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * Override so we can set the view's text _without_ triggering autocomplete.
     *
     * @param text   Just like a normal EditText
     * @param filter true if autocompletion (popup and all) is desired even if
     *               programmatically filled in
     */
    @Override
    public void setText(CharSequence text, boolean filter) {
        if (Build.VERSION.SDK_INT >= 17) {
            super.setText(text, filter);
        } else {
            if (filter) {
                setText(text);
            } else {
                Object adapter = getAdapter();
                setAdapter(null);
                setText(text);
                if (adapter instanceof ArrayAdapter)
                    setAdapter((ArrayAdapter) adapter);
                else
                    setAdapter((CursorAdapter) adapter);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler = null;
    }
}