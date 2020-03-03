package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.reactivestreams.Subscription;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import tw.com.masterhand.gmorscrm.ImageActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.activity.customer.ContactPersonCreateActivity;
import tw.com.masterhand.gmorscrm.model.Phone;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class ContactCard extends LinearLayout {
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.textView_job)
    TextView tvJob;
    @BindView(R.id.textView_department)
    TextView tvDept;
    @BindView(R.id.textView_area)
    TextView tvArea;
    @BindView(R.id.textView_note)
    TextView tvNote;
    @BindView(R.id.textView_wechat)
    TextView tvWechat;
    @BindView(R.id.imageView_icon)
    ImageView ivIcon;
    @BindView(R.id.linearLayout_phone)
    LinearLayout containerPhone;
    @BindView(R.id.linearLayout_mail)
    LinearLayout containerMail;
    @BindView(R.id.container_detail)
    RelativeLayout containerDetail;
    @BindView(R.id.imageButton_phone)
    ImageButton btnPhone;
    @BindView(R.id.imageButton_wechat)
    ImageButton btnWechat;
    @BindView(R.id.containerNameCard)
    LinearLayout containerNameCard;
    @BindView(R.id.btnEdit)
    Button btnEdit;

    Context context;
    ContactPerson contact;
    Listener listener;

    public interface Listener {
        void onEdit();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public ContactCard(Context context) {
        super(context);
        init(context);
    }

    public ContactCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ContactCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.card_contact, this);
        ButterKnife.bind(this, view);
    }

    /**
     * 設定聯絡人資料
     */
    public void setContact(final ContactPerson contact) {
        this.contact = contact;
        if (!TextUtils.isEmpty(contact.getShowName())) {
            tvName.setText(contact.getShowName());
        }
        if (contact.getAddress() != null) {
            tvArea.setText(contact.getAddress().getShowAddress());
        }
        if (!TextUtils.isEmpty(contact.getTitle())) {
            tvJob.setText(contact.getTitle());
        }
        if (!TextUtils.isEmpty(contact.getCustomer_department_name())) {
            tvDept.setText(contact.getCustomer_department_name());
        }
        if (!TextUtils.isEmpty(contact.getDescription())) {
            tvNote.setText(contact.getDescription());
        }
        if (!TextUtils.isEmpty(contact.getWechat())) {
            tvWechat.setText(contact.getWechat());
        }
        if (contact.getTel() != null && contact.getTel().size() > 0) {
            for (Phone phone : contact.getTel()) {
                TextView tvPhone = new TextView(context);
                tvPhone.setText(phone.getShowPhone());
                tvPhone.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                tvPhone.setMovementMethod(LinkMovementMethod.getInstance());
                containerPhone.addView(tvPhone);
            }
        }

        if (contact.getEmail() != null && contact.getEmail().size() > 0) {
            for (String mail : contact.getEmail()) {
                if (TextUtils.isEmpty(mail))
                {
                    continue;
                }

                TextView tvMail = new TextView(context);
                tvMail.setText(mail);
                tvMail.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
                tvMail.setMovementMethod(LinkMovementMethod.getInstance());
                containerMail.addView(tvMail);
            }
        }

        if (!TextUtils.isEmpty(contact.getPhoto())) {
            Bitmap bitmap = Base64Utils.decodeToBitmapFromString(contact.getPhoto());
            ivIcon.setImageDrawable(ImageTools.getCircleDrawable(getResources(), bitmap));
        }

        DatabaseHelper.getInstance(context).getFileByParent(contact.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<File>() {

            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(final File item) {
                containerNameCard.setVisibility(VISIBLE);
                int nameCardWidth = UnitChanger.dpToPx(280);
                int nameCardHeight = UnitChanger.dpToPx(175);
                final ImageView ivNameCard = new ImageView(context);
                ivNameCard.setAdjustViewBounds(true);
                ivNameCard.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (!TextUtils.isEmpty(item.getFile())) {
                    ivNameCard.setImageBitmap(Base64Utils.decodeToBitmapFromString(item.getFile()));
                    ivNameCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ImageActivity.class);
                            intent.putExtra(MyApplication.INTENT_KEY_IMAGE, item.getId());
                            context.startActivity(intent);
                        }
                    });
                } else {
                    ImageLoader.getInstance().displayImage(item.getFileUrl(), ivNameCard);
                    ivNameCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ImageActivity.class);
                            intent.putExtra(MyApplication.INTENT_KEY_IMAGE_URL, item.getFileUrl());
                            context.startActivity(intent);
                        }
                    });
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(nameCardWidth, nameCardHeight);
                containerNameCard.addView(ivNameCard, params);
            }

            @Override
            public void onError(Throwable t) {
                containerNameCard.setVisibility(GONE);
            }

            @Override
            public void onComplete() {

            }
        });
    }


    @OnClick(R.id.btnEdit)
    void edit() {
        Intent intent = new Intent(context, ContactPersonCreateActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_CONTACT, contact.getId());
        context.startActivity(intent);
        if (listener != null)
            listener.onEdit();
    }

    @OnClick(R.id.imageButton_phone)
    void call() {
        if (contact.getTel() != null && contact.getTel().size() > 0) {
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getTel().get(0)));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    @OnClick({R.id.imageButton_wechat, R.id.textView_wechat})
    void openWechat() {
        boolean isWechatExist = false;
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);

        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    isWechatExist = true;
                    break;
                }
            }
        }

        if (isWechatExist) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.setType("text/*");
            intent.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools" + ".ShareImgUI");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google" + ".com/store/apps/details?id=com.tencent.mm"));
            context.startActivity(intent);
        }
    }

    /**
     * 顯示聯絡人明細
     */
    public void showDetail() {
        btnPhone.setVisibility(GONE);
        btnWechat.setVisibility(GONE);
        containerDetail.setVisibility(VISIBLE);
    }

    /**
     * 取得聯絡人物件
     */
    public ContactPerson getContact() {
        return contact;
    }
}
