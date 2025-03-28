package com.speechify.Ssml;

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
        String inputString = unescapeXMLChars(ssml);
        if (inputString.isEmpty()){
            return null;
        }
        // Verify if the input string is valid SSML

        /*if (!inputString.startsWith("<") || !inputString.endsWith(">")){
            throw new IllegalArgumentException("Tags could not be parsed");
        }*/
        if (inputString.startsWith("</") && inputString.endsWith(">")){
            throw new IllegalArgumentException("Tags could not be parsed");
        }
        if(inputString.indexOf("<") == -1 || inputString.indexOf(">") == -1){
            return new SSMLText(inputString);
        }
        // Extract the first tag
        String name = inputString.substring(inputString.indexOf("<")+1, inputString.indexOf(">"));
        System.out.println(" Value of name " + name);
        // Extract the attributes
        // String attributes = inputString.substring(inputString.indexOf("<")+1, inputString.indexOf(">"));
        // Extract the children
        String startTag = "<"+name+">";
        String endTag = "</"+name+">";
        inputString = removeWord(inputString, startTag);
        inputString = removeWord(inputString, endTag);
        if (inputString.isEmpty()){
            return new SSMLElement(name, Collections.emptyList(), Collections.emptyList());
        }
        return new SSMLElement(name, Collections.emptyList(), List.of(parseSSML(inputString)));
    }

    private static String removeWord(String text, String wordToRemove) {
        int index = text.indexOf(wordToRemove);

        if (index != -1) {
            return text.substring(0, index) + text.substring(index + wordToRemove.length());
        }
        return text;
    }

    // Recursively converts SSML node to string and unescapes XML chars
    public static String ssmlNodeToText(SSMLNode node) {
        throw new UnsupportedOperationException("Implement this function");
    }

    // Already done for you
    public static String unescapeXMLChars(String text) {
        return text.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
    }

    public sealed interface SSMLNode permits SSMLElement, SSMLText {}

    public record SSMLElement(String name, List<SSMLAttribute> attributes, List<SSMLNode> children) implements SSMLNode {}

    public record SSMLAttribute(String name, String value) {}

    public record SSMLText(String text) implements SSMLNode {}

    public static void main(String[] args) {
        parseSSML("<speak><p></p></speak>");
    }


}
