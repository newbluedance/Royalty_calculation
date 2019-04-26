package com.lcf.salary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Main {

    private static final String DIR_PATH = "D:/salary/";
    private static List<Dept> depts;

    public static void main(String[] args) {

       /* Dept dept=new Dept();
        BigDecimal[] sr ={new BigDecimal(0),new BigDecimal(10000),new BigDecimal(30000)};
        BigDecimal[] rr ={new BigDecimal(0.1),new BigDecimal(0.15),new BigDecimal(0.2)};
        dept.setSalesRules(Arrays.asList(sr));
        dept.setRoyaltyRules(Arrays.asList(rr));

        Employee emp=new Employee();
        emp.setSales(new BigDecimal(15000));
        emp.setDept(dept);

        BigDecimal decimal = calRoyalty(emp);
        System.out.println(decimal.setScale(0,BigDecimal.ROUND_HALF_UP));*/
        int i = 0;
        List<String> salesFiles = getSalesFile();
        for (String excelUrl : salesFiles) {
            try {
                xlsx_reader(excelUrl, i++);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    //*************xlsx文件读取函数************************
    //excel_name为文件名，arg为需要查询的列号
    //返回二维字符串数组
    static ArrayList<ArrayList<String>> xlsx_reader(String inExcelPath, int n)
        throws IOException {
        FileOutputStream excelFileOutPutStream = new FileOutputStream(DIR_PATH.concat("outPut_" + n + ".xls"));

        //读取xlsx文件
        HSSFWorkbook HSSfWorkbook = null;
        //寻找目录读取文件
        File excelFile = new File(inExcelPath);
        InputStream is = new FileInputStream(excelFile);
        HSSfWorkbook = new HSSFWorkbook(is);

        if (HSSfWorkbook == null) {
            System.out.println("未读取到内容,请检查路径！");
            return null;
        }

        //寻找目录读取文件
        HSSFSheet inSheet = HSSfWorkbook.getSheetAt(0);

        ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();

        // 对于sheet，读取其中的每一行
        for (int rowNum = 1; rowNum <= inSheet.getLastRowNum(); rowNum++) {
            try {
                HSSFRow inSheetRow = inSheet.getRow(rowNum);
                if (inSheetRow == null) {
                    continue;
                }
                int i = 0;
                HSSFCell cellA = inSheetRow.getCell(i++);
                HSSFCell cellB = inSheetRow.getCell(i++);
                if (rowNum == 1) {
                    HSSFRow row = inSheet.getRow(rowNum - 1);
                    row.createCell(i).setCellValue("提成金额");
                }

                //姓名
                String name = cellA.getStringCellValue();
                //销售额
                double sum = cellB.getNumericCellValue();
                //部门名
                String deptName = name.split("-")[1].substring(0, 1);
                //部门
                Dept dept = getDeptByName(deptName);

                Employee emp = new Employee();
                emp.setSales(new BigDecimal(sum));
                emp.setDept(dept);

                BigDecimal tc = calRoyalty(emp);
                inSheetRow.createCell(i).setCellValue(tc.doubleValue());
            } catch (Exception e) {
            }


        }
        HSSfWorkbook.write(excelFileOutPutStream);
        excelFileOutPutStream.flush();
        excelFileOutPutStream.close();
        return ans;
    }


    /**
     * 根据员工销售额 和部门规则信息计算提成金额
     *
     * @param emp
     * @return 提成金额
     */
    static BigDecimal calRoyalty(Employee emp) {
        BigDecimal s = emp.getSales();
        BigDecimal r = BigDecimal.ZERO;
        emp.setRoyalty(r);

        List<BigDecimal> salesRules = emp.getDept().getSalesRules();
        List<BigDecimal> royaltyRules = emp.getDept().getRoyaltyRules();

        for (int i = 1; i <= salesRules.size(); i++) {
            if (i == salesRules.size()) {
                BigDecimal decimal = salesRules.get(i - 1);
                BigDecimal decimal1 = royaltyRules.get(i - 1);
                r = r.add(s.subtract(decimal).multiply(decimal1));
                break;
            }

            if (s.compareTo(salesRules.get(i)) == -1) {
                r = r.add(s.subtract(salesRules.get(i - 1)).multiply(royaltyRules.get(i - 1)));
                break;
            } else {
                r = r.add(salesRules.get(i).subtract(salesRules.get(i - 1)).multiply(royaltyRules.get(i - 1)));
            }
        }

        return r;
    }

    /**
     * 获取人员 销售额文件list
     *
     * @return
     */
    static List<String> getSalesFile() {
        List<String> strings = new ArrayList<>();
        List<String> list = new ArrayList<String>();
        File baseFile = new File(DIR_PATH);

        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                String name = file.getName();
                String[] names = name.split("\\.");
                if (names.length >= 2 && names[0].contains("sales") && names[names.length - 1].equals("xls")) {
                    strings.add(DIR_PATH + name);
                }
            }
        }
        return strings;
    }

    /**
     * 根据部门名称获取部门 提成规则
     *
     * @return
     */
    static Dept getDeptByName(String inName) throws IOException {
        if (depts == null) {
            depts = new ArrayList<>();
            //读取xlsx文件
            HSSFWorkbook HSSfWorkbook = null;
            //寻找目录读取文件
            File excelFile = new File(DIR_PATH.concat("提成规则.xls"));
            InputStream is = new FileInputStream(excelFile);
            HSSfWorkbook = new HSSFWorkbook(is);

            if (HSSfWorkbook == null) {
                System.out.println("未读取到内容,请检查路径！");
                return null;
            }

            //寻找目录读取文件
            HSSFSheet inSheet = HSSfWorkbook.getSheetAt(0);

            // 对于sheet，读取其中的每一行
            for (int rowNum = 1; rowNum <= inSheet.getLastRowNum(); rowNum++) {
                try {
                    HSSFRow inSheetRow = inSheet.getRow(rowNum);
                    if (inSheetRow == null) {
                        continue;
                    }
                    int i = 0;
                    HSSFCell cellA = inSheetRow.getCell(i++);
                    HSSFCell cellB = inSheetRow.getCell(i++);
                    HSSFCell cellC = inSheetRow.getCell(i++);

                    //部门名
                    String name = cellA.getStringCellValue();
                    //金额区间
                    List<BigDecimal> salesRules = getArrayFromStr(cellB.getStringCellValue());
                    //提成区间
                    List<BigDecimal> royaltyRules = getArrayFromStr(cellC.getStringCellValue());
                    Dept dept = new Dept();
                    dept.setName(name);
                    dept.setSalesRules(salesRules);
                    dept.setRoyaltyRules(royaltyRules);
                    depts.add(dept);

                } catch (Exception e) {
                    continue;
                }
            }
        }
        for (Dept dept : depts) {
            if (inName.equals(dept.getName())) {
                return dept;
            }
        }

        return null;
    }

    static List<BigDecimal> getArrayFromStr(String s) {
        String[] arr = s.replace("[", "").replace("]", "").split(",");
        List<BigDecimal> decimals = Arrays.asList(arr).stream().map(t -> new BigDecimal(t))
            .collect(Collectors.toList());
        return decimals;
    }


}
