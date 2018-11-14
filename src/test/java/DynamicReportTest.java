import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Created by pandechuan on 2018/10/31.
 */
public class DynamicReportTest {


    public static void main(String[] args) {

        JasperReportBuilder report = DynamicReports.report();// 创建空报表
        // 样式
        StyleBuilder boldStl = DynamicReports.stl.style().bold();
        StyleBuilder boldCenteredStl = DynamicReports.stl.style(boldStl)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        StyleBuilder titleStl = DynamicReports.stl.style(boldCenteredStl)
                .setFontSize(16);
        StyleBuilder columnTitleStl = DynamicReports.stl.style(boldCenteredStl)
                .setBorder(DynamicReports.stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);

        //List<Map<String, Object>> list = Maps.newHashMap();

        List<Map<String, Object>> list = Lists.newArrayList();

        Map<String, Object> map = Maps.newHashMap();
        map.put("id", 123);
        map.put("code", "185");
        map.put("service", "中国移动");
        map.put("province", "重庆");
        map.put("city", "重庆");
        map.put("type", "apple");
        map.put("name", "测试");
        list.add(map);

        report.columns(
                Columns.column("ID", "id", DataTypes.integerType())
                        .setHorizontalAlignment(HorizontalAlignment.CENTER),// 列
                Columns.column("手机号段", "code", DataTypes.stringType()),
                Columns.column("运营商", "service", DataTypes.stringType()),
                Columns.column("省份", "province", DataTypes.stringType()),
                Columns.column("城市", "city", DataTypes.stringType()),
                Columns.column("品牌", "type", DataTypes.stringType()))
                .setColumnTitleStyle(columnTitleStl)
                .setHighlightDetailEvenRows(true)
                .title(Components.text("手机号段").setStyle(titleStl))
                // 标题
                .pageFooter(Components.pageXofY().setStyle(boldCenteredStl))
                .setDataSource(list);// 数据源
        try {
            // 显示报表
            report.show();
            //report.toXls(new FileOutputStream("F:/test.xls"));
            // 生成PDF文件
            // report.toPdf(new FileOutputStream("F:/test.pdf"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
