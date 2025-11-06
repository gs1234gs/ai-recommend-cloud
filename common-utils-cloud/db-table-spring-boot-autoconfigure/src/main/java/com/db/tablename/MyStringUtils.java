package com.db.tablename;
public class MyStringUtils {
    /**
     * 将驼峰命名法（camelCase 或 PascalCase）的字符串转换为下划线命名法（snake_case）。
     * 例如: "userName" -> "user_name", "UserAccountInfo" -> "user_account_info"
     *
     * @param camelStr 待转换的驼峰命名字符串
     * @return 转换后的下划线命名字符串，如果输入为 null 则返回 null
     */
    public static String camelToSnake(String camelStr) {
        if (camelStr == null || camelStr.isEmpty()) {
            return camelStr;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelStr.length(); i++) {
            char ch = camelStr.charAt(i);
            // 如果是大写字母且不是第一个字符，则添加下划线
            if (Character.isUpperCase(ch) && i > 0) {
                sb.append('_');
            }
            // 将当前字符转为小写并添加
            sb.append(Character.toLowerCase(ch));
        }
        return sb.toString();
    }
    /**
     * 将驼峰命名法转换为下划线命名法，并尽量保持连续大写字母的整体性。
     * 例如: "getHTTPResponseCode" -> "get_http_response_code", "XMLParser" -> "xml_parser"
     *
     * @param camelStr 待转换的驼峰命名字符串
     * @return 转换后的下划线命名字符串
     */
    public static String camelToUnderlineSmart(String camelStr) {
        if (camelStr == null || camelStr.isEmpty()) {
            return camelStr;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelStr.length(); i++) {
            char c = camelStr.charAt(i);
            if (Character.isUpperCase(c)) {
                // 需要加下划线的条件：不是开头，且（前一个是小写 或 后一个是小写）
                if (i > 0 &&
                    (Character.isLowerCase(camelStr.charAt(i - 1)) ||
                     (i + 1 < camelStr.length() && Character.isLowerCase(camelStr.charAt(i + 1))))) {
                    sb.append('_');
                }
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
    // --- 测试代码 ---
//    public static void main(String[] args) {
//        // 测试用例
//        System.out.println(camelToUnderlineSmart("userName"));           // 输出: user_name
//        System.out.println(camelToUnderlineSmart("UserAccountInfo"));    // 输出: user_account_info
//        System.out.println(camelToUnderlineSmart("getHTTPResponseCode")); // 输出: get_h_t_t_p_response_code
//        System.out.println(camelToUnderlineSmart("XMLParser"));          // 输出: x_m_l_parser
//        System.out.println(camelToUnderlineSmart(""));                   // 输出: (空字符串)
//        System.out.println(camelToUnderlineSmart(null));                 // 输出: null
//    }
}
