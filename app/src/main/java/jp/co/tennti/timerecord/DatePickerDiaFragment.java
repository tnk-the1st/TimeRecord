package jp.co.tennti.timerecord;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Date;

/**
 * Created by TENNTI on 2016/04/21.
 */
public class DatePickerDiaFragment  extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.date_picker_dia, container,false);

        DatePicker datePicker = (DatePicker)contentView.findViewById(R.id.datePickerDia);
        // 年を取得
        int year = datePicker.getYear();
        // 月を取得 0～11
        int month = datePicker.getMonth();
        // 日を取得
        int day = datePicker.getDayOfMonth();
        // APIレベル11以降の「日」のカレンダーを表示しない 初期値true
        datePicker.setCalendarViewShown(false);
        // 初期値を設定する 1999年12月31日
        //datePicker.updateDate(1999, 11, 31);
        //2011/01/01~3000/12/31
        Date minDate = new Date(111, 0, 1);
        Date maxDate = new Date(1100, 12, 31);
        datePicker.setMinDate(minDate.getTime());
        datePicker.setMaxDate(maxDate.getTime());
        int day_id = Resources.getSystem().getIdentifier("day", "id", "android");
        datePicker.findViewById(day_id).setVisibility(View.GONE);

        return contentView;
    }
}
