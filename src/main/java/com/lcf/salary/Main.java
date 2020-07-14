package com.lcf.salary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Main {

    private static String DIR_PATH;
    private static List<Dept> depts;

    public static void main(String[] args) {
        try {
            String pathUrl = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            DIR_PATH = URLDecoder.decode(pathUrl, "utf-8");

        } catch (Exception e) {
            DIR_PATH = "D:/salary/";
        }
        System.out.println("当前路径：" + DIR_PATH);

        int i = 0;
        //获取人员 销售额文件list
        List<String> salesFiles = getSalesFile();
        for (String excelUrl : salesFiles) {
            try {
                handleOne(excelUrl, i++);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 处理单个excel
     *
     * @param inExcelPath excel路径
     * @param n           序号
     * @return 返回二维字符串数组
     * @throws IOException
     */
    static ArrayList<ArrayList<String>> handleOne(String inExcelPath, int n)
            throws IOException {

        String substring = inExcelPath.substring(1, inExcelPath.length() - 4);
        FileOutputStream excelFileOutPutStream = new FileOutputStream(substring.concat("_outPut".concat(".xls")));

        //读取xls文件
        HSSFWorkbook hssfWorkbook = null;
        //寻找目录读取文件
        File excelFile = new File(inExcelPath);
        InputStream is = new FileInputStream(excelFile);
        hssfWorkbook = new HSSFWorkbook(is);

        if (hssfWorkbook == null) {
            System.out.println("未读取到内容,请检查路径！");
            return null;
        }

        //寻找目录读取文件
        HSSFSheet inSheet = hssfWorkbook.getSheetAt(0);

        ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();
        //读取首行
        HSSFRow firstRow = inSheet.getRow(0);
        short lastCellNum = firstRow.getLastCellNum();
        //获取列编号
        Integer nameCellNo = null;
        Integer deptCellNo = null;
        Integer salesCellNo = null;


        for (int i = 0; i < lastCellNum; i++) {
            String cellValue = firstRow.getCell(i).getStringCellValue();
            if (cellValue.contains("全名") || "职员".equals(cellValue)) {
                nameCellNo = i;
            } else if (cellValue.contains("部门")) {
                deptCellNo = i;
            } else if (cellValue.contains("合计") || cellValue.contains("金额")) {
                salesCellNo = i;
            }
        }
        String name=null;
        // 对于sheet，读取其中的每一行
        for (int rowNum = 1; rowNum <= inSheet.getLastRowNum(); rowNum++) {
            try {
                HSSFRow inSheetRow = inSheet.getRow(rowNum);
                if (inSheetRow == null) {
                    continue;
                }
                if (rowNum == 1) {
                    HSSFRow row = inSheet.getRow(rowNum - 1);
                    row.createCell(lastCellNum).setCellValue("提成金额");
                }

                //姓名
                name = inSheetRow.getCell(nameCellNo).getStringCellValue();
                //销售额
                double sum = inSheetRow.getCell(salesCellNo).getNumericCellValue();
                //部门名
                String deptName=null;
                if (deptCellNo != null) {
                    deptName= inSheetRow.getCell(deptCellNo).getStringCellValue();
                }else{
                    deptName = name.split("-")[1].split("\\(")[0];
                }
                //根据部门和金额获取 提成规则
                Dept dept = getDeptByName(deptName, sum);

                Employee emp = new Employee();
                emp.setName(name);
                emp.setSales(new BigDecimal(sum));
                emp.setDept(dept);
                //计算提成
                BigDecimal tc = calRoyalty(emp);
                inSheetRow.createCell(lastCellNum).setCellValue(tc.doubleValue());
            } catch (Exception e) {
                System.out.println("当前人名："+name+"数据有误！没有有效的部门规则！");
            }


        }
        hssfWorkbook.write(excelFileOutPutStream);
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
        BigDecimal sales = emp.getSales();
        if (sales.compareTo(BigDecimal.ZERO)==-1){
            return BigDecimal.ZERO;
        }
        BigDecimal r = BigDecimal.ZERO;
        emp.setRoyalty(r);
        // 金额区间 [0,10000,30000]
        List<BigDecimal> salesRules = emp.getDept().getSalesRules();
        // 提成比例区间 [0.1,0.15,0.2]
        List<BigDecimal> royaltyRules = emp.getDept().getRoyaltyRules();

        if (emp.getName().contains("合计")){
            System.out.println(emp.getSales());
        }
//[0,15000,35000,50000]	[0.08,0.1,0.15,0.2]
        //阶梯计算
        for (int i = 1; i <= salesRules.size(); i++) {

            if (i == salesRules.size() || sales.compareTo(salesRules.get(i)) == -1) {
                //r=0+(s-0)*0.1
                //r=r+ (s-10000) *0.15
                r = r.add(sales.subtract(salesRules.get(i - 1)).multiply(royaltyRules.get(i - 1)));
                break;
            } else {
                // r=r+(10000-0)*0.1
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
        File baseFile = new File(DIR_PATH);

        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                String name = file.getName();
                String[] names = name.split("\\.");
                if (names.length >= 2 && !names[0].contains("提成规则") && !names[0].contains("outPut") && names[names.length - 1].equals("xls")) {
                    System.out.println(DIR_PATH + name);
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
    static Dept getDeptByName(String inName, double sum) throws IOException {
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

                    //部门名
                    String name = inSheetRow.getCell(i++).getStringCellValue();
                    //销售金额规则适配
                    HSSFCell rulSales = inSheetRow.getCell(i++);
                    String salesRul = rulSales!=null?rulSales.getStringCellValue():null;
                    //提成金额区间
                    List<BigDecimal> salesRules = getArrayFromStr(inSheetRow.getCell(i++).getStringCellValue());
                    //提成区间
                    List<BigDecimal> royaltyRules = getArrayFromStr(inSheetRow.getCell(i++).getStringCellValue());
                    Dept dept = new Dept();
                    dept.setName(name);
                    dept.setSalesRules(salesRules);
                    dept.setRoyaltyRules(royaltyRules);
                    dept.setSalesRul(salesRul);
                    depts.add(dept);

                } catch (Exception e) {
                    continue;
                }
            }
        }
        for (Dept dept : depts) {
            if (inName.equals(dept.getName())) {
                if (null == dept.getSalesRul() || "".equals(dept.getSalesRul().trim())) {
                    return dept;
                } else if (dept.getSalesRul().contains("-")) {
                    String[] split = dept.getSalesRul().split("-");
                    if (sum >= Double.parseDouble(split[0]) && sum < Double.parseDouble(split[1])) {
                        return dept;
                    }
                } else if (dept.getSalesRul().contains("+")) {
                    String rnum = dept.getSalesRul().replace("\\+", "");
                    if (sum >= Double.parseDouble(rnum)) {
                        return dept;
                    }
                } else {
                    System.out.println("没有找到匹配的规则！");
                }
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
