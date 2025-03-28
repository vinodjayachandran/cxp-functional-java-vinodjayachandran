package com.speechify.Ssml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SSML (Speech Synthesis Markup Language) is a subset of XML specifically
 * designed for controlling synthesis. You can see examples of how the SSML
 * should be parsed in com.speechify.SSMLTest in `src/test/java/SSMLTest.java`.
 *
 * You may:
 *  - Read online guides to supplement information given in com.speechify.SSMLTest to understand SSML syntax.
 * You must not:
 *  - Use XML parsing libraries or the DocumentBuilderFactory. The task should be solved only using string manipulation.
 *  - Read guides about how to code an XML or SSML parser.
 */
public class Ssml {

    // Parses SSML to a SSMLNode, throwing on invalid SSML
    public static SSMLNode parseSSML(String ssml) {
        // NOTE: Don't forget to run unescapeXMLChars on the SSMLText

        if(!validateInputSSML(ssml)){
            throw new IllegalArgumentException("Tags could not be parsed");
        }
//        String inputString = unescapeXMLChars(ssml);
        return getSSMLNode(ssml);
    }

    private static boolean validateInputSSML(String inputString) {
        return inputString.startsWith("<speak") && inputString.endsWith("</speak>");
    }

    private static SSMLNode getSSMLNode(String inputString) {
        if (inputString.isEmpty()){
            return null;
        }
        // Verify if the input string is valid SSML
        if (!inputString.startsWith("<") || !inputString.endsWith(">")){
            throw new IllegalArgumentException("Tags could not be parsed");
        }

        if(inputString.indexOf("<") == -1 || inputString.indexOf(">") == -1){
            return new SSMLText(unescapeXMLChars(inputString));
        }
        // Extract the first tag
        String name = inputString.substring(inputString.indexOf("<")+1, inputString.indexOf(">")).trim();


        // Extract the attributes
        System.out.println(" Value of name " + name);
        String startTag = "<"+name+">";
        // check if attributes exist
        List<SSMLAttribute> ssmlAttributes = new ArrayList<>();
        if (name.contains(" ")){
            String attributeString = name.substring(name.indexOf(" ")+1);
            if (!attributeString.contains("=")){
                throw new IllegalArgumentException("Attributes could not be parsed");
            }
            while (attributeString.contains("=")){

                String key = attributeString.substring(0, attributeString.indexOf("=")).trim();
                if(key.isEmpty()){
                    throw new IllegalArgumentException("Attributes could not be parsed");
                }
                System.out.println( " Key " + key);
                attributeString = attributeString.substring(attributeString.indexOf("=")+1).trim();
                // value end index can either be end of string and space
                int endIndex = !attributeString.contains(" ") ?attributeString.length():attributeString.indexOf(" ");
                System.out.println("End Index " + endIndex);
                // value must contain 2 double quotes
//                String value = attributeString.substring(0, endIndex).trim().replaceAll("\"","");
                String value = attributeString.substring(0, endIndex).trim();
                if(countDoubleQuotes(value)!=2){
                    throw new IllegalArgumentException("Attributes could not be parsed");
                }
                value = value.replaceAll("\"", "");
                System.out.println("Key " + key + " Value " + value);
                ssmlAttributes.add(new SSMLAttribute(key, value));
                attributeString = attributeString.substring(endIndex);
                System.out.println("Attribute String " + attributeString);
            }

            // Remove Attributes from name if exists
            name = name.substring(0, name.indexOf(" "));
        }

        // Extract the children

        String endTag = "</"+name+">";
        // Validate if startTage and EndTag exists in InputString
        if (!inputString.contains(startTag) || !inputString.contains(endTag)){
            throw new IllegalArgumentException("Tags could not be parsed");
        }
        String body = inputString.substring(startTag.length(), inputString.lastIndexOf(endTag));
        if (body.isEmpty()){
            return new SSMLElement(name, ssmlAttributes, Collections.emptyList());
        }
        // if body doesn't contain Tags, attach it to Body

        if((body.indexOf("<") == -1 && body.indexOf(">") == -1)){
            return new SSMLElement(name, ssmlAttributes, List.of(new SSMLText(unescapeXMLChars(body))));
        }
        if((body.indexOf("<") == -1 && body.indexOf(">") != -1) ||
                (body.indexOf("<") != -1 && body.indexOf(">") == -1)){
            throw new IllegalArgumentException("Tags could not be parsed");
        }

        // Handle Text before and after Children

        String before = body.substring(0, body.indexOf("<"));
        System.out.println("Before " + before );
        String after = body.substring(body.lastIndexOf(">")+1);
        System.out.println("after " + after );
        System.out.println("Value of Children " + body.substring(before.length(), body.lastIndexOf(">")+1));
        String children = body.substring(before.length(), body.lastIndexOf(">")+1);
        ArrayList<SSMLNode> childNodes = new ArrayList<>();
        if (!before.isEmpty()){
            childNodes.add(new SSMLText(unescapeXMLChars(before)));
        }
        if (!children.isEmpty()){
            childNodes.add(getSSMLNode(children));
        }
        if (!after.isEmpty()){
            childNodes.add(new SSMLText(unescapeXMLChars(after)));
        }

        return new SSMLElement(name, ssmlAttributes, childNodes);
    }

    private static int countDoubleQuotes(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '"') {
                count++;
            }
        }
        return count;
    }

    // Recursively converts SSML node to string and unescapes XML chars
    public static String ssmlNodeToText(SSMLNode node) {
        List<SSMLText> ssmlTextList = getSSMLTextList(node);
        StringBuffer stringBuffer = new StringBuffer();
        for (SSMLText ssmlText : ssmlTextList){
            stringBuffer.append(ssmlText.text());
        }
        return stringBuffer.toString();
    }

    private static List<SSMLText> getSSMLTextList(SSMLNode node) {
        List<SSMLText> ssmlTextList = new ArrayList<>();
        if (node instanceof SSMLText){
            ssmlTextList.add((SSMLText) node);
        } else if (node instanceof SSMLElement) {
            // Iterate over children
            SSMLElement ssmlElement = (SSMLElement) node;
            for (SSMLNode child : ssmlElement.children()){
                ssmlTextList.addAll(getSSMLTextList(child));
            }
        }
        return ssmlTextList;
    }

    // Already done for you
    public static String unescapeXMLChars(String text) {
        return text.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
    }

    public sealed interface SSMLNode permits SSMLElement, SSMLText {}

    public record SSMLElement(String name, List<SSMLAttribute> attributes, List<SSMLNode> children) implements SSMLNode {}

    public record SSMLAttribute(String name, String value) {}

    public record SSMLText(String text) implements SSMLNode {}

}
