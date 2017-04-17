package ca.taoxie.findmytrails;

/**
 * Created by Emily on 11/1/2016.
 */

public class BadWordFilter {

    String original;
    String result = "";

    private static final String[] badWords = {"ugly", "darn", "suck","fuck","duck","poop"};


    public BadWordFilter(String original) {
        this.original = original;
        filter();
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private void filter() {
        String delims = " ";
        String [] tokens = original.split(delims);
        int tokenSize = tokens.length;
        boolean badword = false;

        for(int i=0; i < tokenSize; i++){
            String word = tokens[i];
            for(int j=0; j<badWords.length; j++){
                if(badWords[j].equalsIgnoreCase(word)){
                    badword = true;
                }
            }
            if (badword == true){
                result += "**** ";
                badword = false;
            }
            else{
                result += word + " ";
            }
        }

    }

    public String wordCrunch() {
        return result;
    }
}
