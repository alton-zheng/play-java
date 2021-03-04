package structure.impl;

import structure.interfaces.Stack;

/**
 * @Author: alton
 * @Date: Created in 2021/3/3 5:09 下午
 * @Description: 解决了一个场景，括号匹配
 */
public class StackSolution {

    /**
     * 解决了一个场景，括号匹配
     * leetcode 020 问题，括号匹配
     */
    public boolean isValid(String s) {

        Stack<Character> stack = new ArrayStack<>();

        for( int i = 0 ; i < s.length() ; i++) {
            char c = s.charAt(i);

            if (c == '(' || c == '{' || c == '[') {
                stack.push(s.charAt(i));
            } else {

                if (stack.empty()) {
                    return false;
                }

                char topChar = stack.pop();
                if (c == ')' && topChar != '(') {
                    return false;
                }

                if (c == ']' && topChar != '[') {
                    return false;
                }

                if (c == '}' && topChar != '{') {
                    return false;
                }
            }
        }

        return stack.empty();
    }

}
