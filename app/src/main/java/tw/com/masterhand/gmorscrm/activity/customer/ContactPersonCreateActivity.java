package tw.com.masterhand.gmorscrm.activity.customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.reactivestreams.Subscription;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.LocationSelectActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.FileType;
import tw.com.masterhand.gmorscrm.model.Phone;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCity;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCountry;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.ImageHelper;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.ItemInputEmail;
import tw.com.masterhand.gmorscrm.view.ItemInputPhone;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectLocation;

public class ContactPersonCreateActivity extends BaseUserCheckActivity implements ItemInputPhone
        .SelectPhoneTypeListener, ItemSelectLocation.OnSelectListener {

    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectCustomer)
    ItemSelectCustomer itemSelectCustomer;
    @BindView(R.id.containerNameCard)
    LinearLayout containerNameCard;
    @BindView(R.id.button_photo)
    Button btnPhoto;
    @BindView(R.id.itemSelectLocation)
    ItemSelectLocation itemSelectLocation;
    @BindView(R.id.button_name_card)
    Button btnNameCard;
    @BindView(R.id.editText_last_name)
    EditText etLastName;
    @BindView(R.id.editText_name)
    EditText etName;
    @BindView(R.id.etDivision)
    EditText etDivision;
    @BindView(R.id.editText_job)
    EditText etJob;
    @BindView(R.id.editText_note)
    EditText etNote;
    @BindView(R.id.etWeChat)
    EditText etWeChat;
    @BindView(R.id.containerPhone)
    LinearLayout containerPhone;
    @BindView(R.id.itemInputEmail)
    ItemInputEmail itemInputEmail;

    ContactPerson contact;
    ImageHelper imageHelper;
    boolean isNameCard = false;
    ArrayList<File> nameCards;
    ItemInputPhone selectedItemInputPhone = null;
    boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_person_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (!isLoaded) {
            String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
            setCustomer(customerId);
            String contactId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CONTACT);
            if (TextUtils.isEmpty(contactId)) {
                // 新增客戶聯絡人
                contact = new ContactPerson();
                addPhone(null, true);
                itemInputEmail.addEmail("");
                isLoaded = true;
            } else {
                // 編輯客戶聯絡人
                getContact(contactId);
            }
        }
    }

    private void init() {
        imageHelper = new ImageHelper(this);
        nameCards = new ArrayList<>();
        itemSelectLocation.setOnSelectListener(this);
        appbar.setTitle(getString(R.string.title_activity_contact_create));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 點擊完成
                if (checkInput()) {
                    contact.setCustomer_id(itemSelectCustomer.getCustomer().getId());
                    String lastName = etLastName.getText().toString();
                    String name = etName.getText().toString();
                    String job = etJob.getText().toString();
                    String division = etDivision.getText().toString();
                    String note = etNote.getText().toString();
                    String wechat = etWeChat.getText().toString();

                    contact.setLast_name(lastName);
                    contact.setFirst_name(name);
                    contact.setTitle(job);
                    contact.setCustomer_department_name(division);
                    contact.setDescription(note);
                    contact.setWechat(wechat);
                    contact.setAddress(itemSelectLocation.getAddress());

                    ArrayList<Phone> phoneList = new ArrayList<>();
                    for (int i = 0; i < containerPhone.getChildCount(); i++) {
                        ItemInputPhone itemInputPhone = (ItemInputPhone) containerPhone.getChildAt(i);
                        Phone phone = itemInputPhone.getPhone();

                        if (!TextUtils.isEmpty(phone.getType()) && !TextUtils.isEmpty(phone.getTel())) {
                            phoneList.add(phone);
                        }
                    }
                    contact.setTel(phoneList);
                    contact.setEmail(itemInputEmail.getEmail());
                    saveContactPerson();
                }
            }
        });
    }

    //選擇客戶
    private void setCustomer(String customerId) {
        if (!TextUtils.isEmpty(customerId)) {
            mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Customer>() {
                        @Override
                        public void accept(Customer customer) throws Exception {
                            itemSelectCustomer.setCustomer(customer);
                            itemSelectLocation.setAddress(customer.getAddress());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(ContactPersonCreateActivity.this, throwable);
                        }
                    }));
        }
    }

    private void getContact(final String contactId) {
        getNameCard(contactId);
        mDisposable.add(DatabaseHelper.getInstance(this).getContactPersonById(contactId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ContactPerson>() {
                    @Override
                    public void accept(ContactPerson contactPerson) throws Exception {
                        contact = contactPerson;
                        isLoaded = true;
                        setCustomer(contactPerson.getCustomer_id());
                        etName.setText(contactPerson.getFirst_name());
                        etLastName.setText(contactPerson.getLast_name());
                        etDivision.setText(contactPerson.getCustomer_department_name());
                        etJob.setText(contactPerson.getTitle());
                        if (!TextUtils.isEmpty(contactPerson.getDescription()))
                            etNote.setText(contactPerson.getDescription());
                        if (!TextUtils.isEmpty(contactPerson.getWechat()))
                            etWeChat.setText(contactPerson.getWechat());
                        if (contactPerson.getAddress() != null)
                            itemSelectLocation.setAddress(contactPerson.getAddress());

                        if (contactPerson.getEmail().size() > 0) {
                            for (String mail : contactPerson.getEmail()) {
                                itemInputEmail.addEmail(mail);
                            }
                        } else {
                            itemInputEmail.addEmail("");
                        }
                        if (contactPerson.getTel().size() == 0) {
                            addPhone(null, true);
                        } else {
                            int index = 0;
                            for (Phone phone : contactPerson.getTel()) {
                                if (index == 0) {
                                    addPhone(phone, true);
                                } else {
                                    addPhone(phone, false);
                                }
                                index++;
                            }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ContactPersonCreateActivity.this, throwable);
                    }
                }));
    }

    private void getNameCard(String contactId) {
        DatabaseHelper.getInstance(this).getFileByParent(contactId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<File>() {

            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(final File item) {
                generateNameCard(item);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void generateNameCard(final File file) {
        int nameCardWidth = UnitChanger.dpToPx(280);
        int nameCardHeight = UnitChanger.dpToPx(175);
        containerNameCard.setVisibility(View.VISIBLE);
        final ImageView ivNameCard = new ImageView(this);
        ivNameCard.setAdjustViewBounds(true);
        ivNameCard.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (!TextUtils.isEmpty(file.getFile())) {
            ivNameCard.setImageBitmap(Base64Utils.decodeToBitmapFromString(file.getFile()));
        } else {
            ImageLoader.getInstance().displayImage(file.getFileUrl(), ivNameCard);
        }
        ivNameCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showNameCardDeleteAlert(ivNameCard, file);
                return true;
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(nameCardWidth, nameCardHeight);
        containerNameCard.addView(ivNameCard, params);
        nameCards.add(file);
    }

    private void saveContactPerson() {
        mDisposable.add(DatabaseHelper.getInstance(this).saveContactPerson(contact, nameCards)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        Log.e(TAG, "save ContactPerson id:" + result);
                        setResult(RESULT_OK);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(ContactPersonCreateActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
    }

    @Override
    public void onSelectPhoneType(ItemInputPhone item) {
        selectedItemInputPhone = item;
    }

    /**
     * 新增電話
     */
    private void addPhone(Phone phone, boolean isAdd) {
        final ItemInputPhone itemInputPhone = new ItemInputPhone(this);
        if (phone != null)
            itemInputPhone.setPhone(phone);
        itemInputPhone.setSelectPhoneTypeListener(this);
        if (isAdd) {
            itemInputPhone.setFunctionListener(R.mipmap.common_add, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPhone(null, false);
                }
            });
        } else {
            itemInputPhone.setFunctionListener(R.mipmap.common_removeitem, new View
                    .OnClickListener() {
                @Override
                public void onClick(View v) {
                    containerPhone.removeView(itemInputPhone);
                }
            });
        }
        containerPhone.addView(itemInputPhone);
    }


    /**
     * 檢查輸入資料
     */
    private boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etLastName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.last_name), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.name), Toast.LENGTH_LONG).show();
            return false;
        }
        if (itemSelectCustomer.getCustomer() == null) {
            Toast.makeText(this, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return false;
        }
        if (itemSelectLocation.getAddress().getCountry() == null) {
            Toast.makeText(this, R.string.error_msg_no_location, Toast.LENGTH_LONG).show();
            return false;
        }

        ArrayList<Phone> phoneList = new ArrayList<>();
        for (int i = 0; i < containerPhone.getChildCount(); i++) {
            ItemInputPhone itemInputPhone = (ItemInputPhone) containerPhone.getChildAt(i);
            Phone phone = itemInputPhone.getPhone();
            if (!TextUtils.isEmpty(phone.getType()) && !TextUtils.isEmpty(phone.getTel())) { phoneList.add(phone);
            }
        }

        if (phoneList.size() == 0) {
            Toast.makeText(this, required + getString(R.string.phone), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * 顯示照片取得方式對話框
     */
    void showPhotoDialog() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 有相機功能，詢問使用者是要拍攝相片還是開啟相簿
            AlertDialog.Builder builder = new AlertDialog.Builder(this, MyApplication.DIALOG_STYLE);
            builder.setTitle(R.string.select_photo_title);
            builder.setItems(getResources().getStringArray(R.array.select_photo), new
                    DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    imageHelper.takePhoto();
                                    break;
                                case 1:
                                    imageHelper.selectPhoto();
                                    break;
                            }
                        }
                    });
            builder.create().show();
        } else {
            imageHelper.selectPhoto();
        }
    }

    @OnClick(R.id.button_photo)
    /**
     * 大頭照
     */
    void takeContactPhoto() {
        isNameCard = false;
        showPhotoDialog();
    }

    @OnClick(R.id.button_name_card)
    /**
     * 名片拍照
     */
    void takeNameCardPhoto() {
        isNameCard = true;
        showPhotoDialog();
    }

    private void showNameCardDeleteAlert(final ImageView ivNameCard, final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, MyApplication.DIALOG_STYLE);
        builder.setTitle(R.string.title_delete_name_card).setMessage(R.string
                .msg_delete_name_card).setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        containerNameCard.removeView(ivNameCard);
                        nameCards.remove(nameCards.indexOf(file));
                        if (containerNameCard.getChildCount() == 0) {
                            containerNameCard.setVisibility(View.GONE);
                        }
                    }
                });
        builder.create().show();
    }

    @Override
    public void onSelectCountry() {
        Intent intent = new Intent(this, LocationSelectActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_COUNTRY);
    }

    @Override
    public void onSelectCity(String countryId) {
        Intent intent = new Intent(this, LocationSelectActivity.class);
        intent.putExtra(LocationSelectActivity.KEY_COUNTRY, countryId);
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_CITY);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri photoUri = null;
            Bitmap bitmap = null;
            int nameCardWidth = UnitChanger.dpToPx(280);
            int nameCardHeight = UnitChanger.dpToPx(175);
            switch (requestCode) {
                // 選擇圖片
                case ImageHelper.REQUEST_SELECT_PICTURE:
                    java.io.File file = new java.io.File(imageHelper.getImageFilePath(this, data.getData()));
                    photoUri = Uri.fromFile(file);
                    if (isNameCard) {
                        bitmap = imageHelper.getBitmapFromUri(photoUri, nameCardWidth, nameCardHeight);
                        File mFile = new File();
                        mFile.setType(FileType.PHOTO);
                        mFile.setName(getString(R.string.name_card));
                        mFile.setFile(Base64Utils.encodeBitmapToString(bitmap));
                        generateNameCard(mFile);
                    } else {
                        int photoSize = getResources().getDimensionPixelSize(R.dimen
                                .photo_upload_size_small);
                        bitmap = imageHelper.getBitmapFromUri(photoUri, photoSize, photoSize);
                        Drawable roundedBitmapDrawable = ImageTools.getCircleDrawable
                                (getResources(), bitmap);
                        btnPhoto.setBackground(roundedBitmapDrawable);
                        btnPhoto.setText("");
                        contact.setPhoto(Base64Utils.encodeBitmapToString(ImageTools.getCroppedBitmap(bitmap)));
                    }
                    break;
                // 拍攝照片
                case ImageHelper.REQUEST_TAKE_PHOTO:
                    photoUri = Uri.fromFile(imageHelper.getImageFile());
                    if (isNameCard) {
                        bitmap = imageHelper.getBitmapFromUri(photoUri, nameCardWidth, nameCardHeight);
                        File mFile = new File();
                        mFile.setType(FileType.PHOTO);
                        mFile.setName(getString(R.string.name_card));
                        mFile.setFile(Base64Utils.encodeBitmapToString(bitmap));
                        generateNameCard(mFile);
                    } else {
                        int photoSize = getResources().getDimensionPixelSize(R.dimen
                                .photo_upload_size_small);
                        bitmap = imageHelper.getBitmapFromUri(photoUri, photoSize, photoSize);
                        Drawable roundedBitmapDrawable = ImageTools.getCircleDrawable(getResources(), bitmap);
                        btnPhoto.setBackground(roundedBitmapDrawable);
                        btnPhoto.setText("");
                        contact.setPhoto(Base64Utils.encodeBitmapToString(ImageTools.getCroppedBitmap(bitmap)));
                    }
                    break;
                // 選擇電話類型
                case MyApplication.REQUEST_SELECT_PHONE_TYPE:
                    String type = data.getStringExtra(MyApplication.INTENT_KEY_PHONE_TYPE);
                    selectedItemInputPhone.setType(type);
                    break;
                // 選擇國家
                case MyApplication.REQUEST_SELECT_COUNTRY:
                    ConfigCountry config = gson.fromJson(data.getStringExtra(LocationSelectActivity.KEY_RESULT), ConfigCountry.class);
                    itemSelectLocation.selectCountry(config);
                    break;
                // 選擇城市
                case MyApplication.REQUEST_SELECT_CITY:
                    ConfigCity configCity = gson.fromJson(data.getStringExtra(LocationSelectActivity.KEY_RESULT), ConfigCity.class);
                    itemSelectLocation.selectCity(configCity);
                    break;

                default:
                    itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
