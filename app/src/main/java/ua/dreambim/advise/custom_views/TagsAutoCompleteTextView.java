package ua.dreambim.advise.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ua.dreambim.advise.R;

/**
 * Created by hasana on 12/5/2016.
 */

public class TagsAutoCompleteTextView extends MultiAutoCompleteTextView implements AdapterView.OnItemClickListener {

    private SpannableStringBuilder sb = new SpannableStringBuilder();
    private int mCursorPosition;
    private int mCursorPreviousPosition;
    private List<Tag> mTags = new ArrayList<>();
    private Set<String> mTagsTextSet = new HashSet<>();
    private static final String TAG = "TagsAutoComplete";
    private String[] mMandatoryTags;
    private static final int TAG_MAX_NUMBER = 10;
    private boolean mTextWatcherEnabled = true;
    private boolean mTagAddedFromOutside=false;
    private OnTagEventListener mOnTagEventListener;


    private final TextWatcher mTextWatcher = new TextWatcher() {
        boolean deleting = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //deleting last tag, becouse user click backspace
            if (mTextWatcherEnabled && mTags.size() > 0 && count > 0 && after == 0) {
                deleting = true;
                Log.i(TAG, "las tag posotion is: " + sb.getSpanEnd(mTags.get(mTags.size() - 1).imageSpan));
                if ((start + count) == sb.getSpanEnd(mTags.get(mTags.size() - 1).imageSpan)) {

                    Log.i(TAG, "tag deleted");
                    Log.i(TAG, "s=" + s + ", charAt(start)=" + s.charAt(start) + ", start=" + start + ", after=" + after + ", count=" + count);
                    Log.i(TAG, "las tag posotion is: " + sb.getSpanEnd(mTags.get(mTags.size() - 1).imageSpan));
                    deleteTag(null, mTags.get(mTags.size() - 1));
                }
            }

//            Log.i(TAG, "s=" + s + ", start=" + start + ", after=" + after + ", count=" + count);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!deleting && mTextWatcherEnabled) {
                if (count >= 1) {
                    if (s.charAt(start + count - 1) == ',' || s.charAt(start + count - 1) == '\n' || s.charAt(start + count - 1) == ' ') {
                        drawTag();
                    }
                }
                Log.i(TAG, "s=" + s + ", start=" + start + ", before=" + before + ", count=" + count);
            } else {
                deleting = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (getText() != null) {
            setSelection(getText().length());
        } else {
            super.onSelectionChanged(selStart, selEnd);
        }
    }

    public void setMandatroyTags(String[] mandatoryTags) {
        if (mandatoryTags != null) {
            this.mMandatoryTags = mandatoryTags;
        } else {
            this.mMandatoryTags = new String[0];
        }
    }

    public String[] getTags(){
        String[] tagsArray=new String[mTags.size()];
        for(int i=0;i<mTags.size();i++){
            tagsArray[i]=mTags.get(i).getText();
        }
        return tagsArray;
    }

    public void drawTag() {
        if (mTags.size() <= TAG_MAX_NUMBER) {
            int restoreCursorPosition = mCursorPosition;
            mCursorPreviousPosition = restoreCursorPosition;
            mCursorPosition = getText().length();
            CharSequence tagCharSeq;
            //if user delete space and insert tag then add space before tag
            if (mCursorPreviousPosition > 0 && getText().charAt(mCursorPreviousPosition - 1) != ' ') {
                mTextWatcherEnabled = false;
                setText(getText().insert(mCursorPreviousPosition - 1, " "));
                mTextWatcherEnabled = true;
                mCursorPosition++;
            }
            tagCharSeq = getText().subSequence(mCursorPreviousPosition, mCursorPosition - 1);
            //if tag is not entered before and it is not empty
            if (!isInTagSet(tagCharSeq) && !tagCharSeq.toString().equals("")) {

                Tag tag = new Tag(tagCharSeq.toString(), mCursorPreviousPosition, mCursorPreviousPosition + tagCharSeq.length());
                addTag(tag);

                setSelection(getText().length() - 1);

            } else {
                Toast.makeText(getContext(), R.string.create_article_activity_entered_the_same_text_message, Toast.LENGTH_LONG).show();
                revertLastCharacter();
                mCursorPosition = restoreCursorPosition;
            }
        } else {
            Toast.makeText(getContext(), "tags max size is:=" + TAG_MAX_NUMBER, Toast.LENGTH_LONG).show();
        }

    }

    private void revertLastCharacter() {
        mTextWatcherEnabled = false;
        getText().delete(getText().length() - 1, getText().length());
        mCursorPosition--;
        setSelection(getText().length());
        mTextWatcherEnabled = true;
    }

    private boolean isInTagSet(CharSequence tagCharSeq) {
        return mTagsTextSet.contains(tagCharSeq.toString());
    }

    public void appendTagByOutsideClick(String text){
        mTagAddedFromOutside=true;
        append(text);
    }
    public void setOnTagDeletedListener(OnTagEventListener listener){
        this.mOnTagEventListener = listener;
    }

    private void addTag(final Tag tag) {
        sb.append(tag.getText()).append(" ");
//        sb=new SpannableStringBuilder(getText());
        TextView tv = createContactTextView(tag.getText());
        Drawable bd = convertViewToDrawable(tv);
        bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
        ImageSpan imageSpan = new ImageSpan(bd);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                deleteTag(widget, tag);
            }
        };
        tag.setImageSpan(imageSpan);
        tag.setClickableSpan(clickableSpan);
        sb.setSpan(imageSpan, tag.getStartPosition(), tag.getEndPosition(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(clickableSpan, tag.getStartPosition(), tag.getEndPosition(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextWatcherEnabled = false;
        setText(sb);
        mTextWatcherEnabled = true;
        mTags.add(tag);
        mTagsTextSet.add(tag.getText());
        if(mOnTagEventListener!=null){
            mOnTagEventListener.added(tag.getText());
        }
    }

    private void deleteTag(View widget, Tag tag) {
        //+1 for last charaqter (space charaqter included when tags added)
        int deleteTagLength = tag.getEndPosition() - tag.getStartPosition() + 1;

        sb.delete(tag.getStartPosition(), tag.getEndPosition() + 1);
        sb.removeSpan(tag.imageSpan);
        sb.removeSpan(tag.clickableSpan);
        mTagsTextSet.remove(tag.getText());
        mTextWatcherEnabled = false;
        setText(sb);
        mTextWatcherEnabled = true;
        for (int i = 0; i < mTags.size(); i++) {
            Tag tag1 = mTags.get(i);
            if (tag1.getStartPosition() > tag.getStartPosition()) {
                tag1.startPosition -= deleteTagLength;
                tag1.endPosition -= deleteTagLength;
            }
        }
        mCursorPosition -= deleteTagLength;
        mTags.remove(tag);
        if(mOnTagEventListener !=null) {
            mOnTagEventListener.deleted(tag.text);
        }
    }

    public TagsAutoCompleteTextView(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public TagsAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public TagsAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(null, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TagsAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }


    public TextView createContactTextView(String text) {
        //creating textview dynamically
        LayoutInflater inflate = LayoutInflater.from(getContext());
        TextView tv;
        if (isMandatory(text)) {
            LinearLayout linearLayout = (LinearLayout) inflate.inflate(R.layout.tag_mandatory, null);
            tv = (TextView) linearLayout.getChildAt(0);
        } else {
            LinearLayout linearLayout = (LinearLayout) inflate.inflate(R.layout.tag_custom, null);
            tv = (TextView) linearLayout.getChildAt(0);
        }
        tv.setText(text);
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_white_18dp, 0);
        return tv;
    }

    private boolean isMandatory(String text) {
        for (String tag : mMandatoryTags) {
            if (text.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    private Drawable convertViewToDrawable(View view) {
        int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(getResources(), viewBmp);
    }


    private void init(@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setMovementMethod(LinkMovementMethod.getInstance());
        setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        addTextChangedListener(mTextWatcher);
        setOnItemClickListener(this);
        setTokenizer(myCommaTokenizer);


//        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
//        if (viewTreeObserver.isAlive()) {
//            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    } else {
//                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                    }
//                    addTextChangedListener(mTextWatcher);
//                    mTextWatcher.afterTextChanged(getText());
//                }
//            });
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        drawTag();
    }

    Tokenizer myCommaTokenizer = new Tokenizer() {
        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != ' ') {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }

            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == ' ') {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        public CharSequence terminateToken(CharSequence text) {
            return text + ",";
        }
    };

    public interface OnTagEventListener {
        void deleted(String tagText);
        void added(String text);
    }


    class Tag {
        private String text;
        private int startPosition;
        private int endPosition;
        private ImageSpan imageSpan;
        private ClickableSpan clickableSpan;

//        private boolean

        Tag(String text, int startPosition, int endPosition) {
            this.text = text;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }

        public void setImageSpan(ImageSpan imageSpan) {
            this.imageSpan = imageSpan;
        }

        public void setClickableSpan(ClickableSpan clickableSpan) {
            this.clickableSpan = clickableSpan;
        }

        public String getText() {
            return text;
        }

        public int getStartPosition() {
            return startPosition;
        }

        public int getEndPosition() {
            return endPosition;
        }
    }
}