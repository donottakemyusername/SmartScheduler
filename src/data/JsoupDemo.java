package data;

import InfoNeeded.Section;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static Support.Term.*;

public class JsoupDemo {

    private String myURL;
    private String profURL;
    private String type;
    private String day;
    private int startingTime;
    private int endingTime;
    private String comments;
    private Elements temp1;
    private Elements temp2;
    Elements result;

    public void dataScraping(String url) {
        myURL = url;
        profURL = splitURL(myURL);
        try {
            findProf(profURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Document doc = Jsoup.connect(myURL).get();
            //print title
            String title = doc.title();
            System.out.println(title);

            temp1 = doc.select(".section1");
            temp2 = doc.select(".section2");

            // interleaving two sections from online html
            Iterator<Element> l1 = temp1.iterator();
            Iterator<Element> l2 = temp2.iterator();
            result = new Elements();
            while (l1.hasNext() || l2.hasNext()) {
                if (l1.hasNext()) {
                    result.add(l1.next());
                }
                if (l2.hasNext()) {
                    result.add(l2.next());
                }
            }
//            System.out.println(result.get(1).child(2).text());
//            System.out.println(result.get(0).child(1).text());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getURL() {
        return myURL;
    }

    public String getProfURL(){
        return profURL;
    }

    //return a list of sections
    public ArrayList getSections() {
        ArrayList<Section> my_list = new ArrayList<>();
        if (result.get(1).child(1).text().isEmpty()) {
            fullYearCourse(my_list);
        } else {
            termCourse(my_list);
        }
        return my_list;
    }

    public ArrayList fullYearCourse(ArrayList<Section> list) {
        for (int k = 0; k < result.size(); k += 2) {
            Section mySection = setSection(k);
            mySection.setTerm(YEAR_TERM);
            list.add(mySection);
        }
        return list;
    }

    public ArrayList termCourse(ArrayList<Section> list){
        for (int i = 0; i < result.size(); i++) {
            Section mySection = setSection(i);
            String curTerm = result.get(i).child(3).text();
            if (Integer.parseInt(curTerm) == 1) {
                mySection.setTerm(TERM_1);
            } else {
                mySection.setTerm(TERM_2);
            }
            list.add(mySection);
        }
        return list;
    }

    public Section setSection(int index){
        Section mySection = new Section();
            String curString = result.get(index).child(1).text();
            mySection.setTitle((curString + " ").split(" ")[2]);
            mySection.setProfURL(profURL);
        return mySection;
    }

    public String splitURL(String thatURL) {
        String[] split0 = thatURL.split("-");
        String[] split1 = split0[1].split("&");
        return (split0[0] + "-section&" + split1[1] + "&" + split1[2] + "&section=");
    }

    public String findProf(String url) throws IOException {
        String name;
        String theURL = getProfURL()+"203";
        Document dc = Jsoup.connect(theURL).get();
        Elements body = dc.select(".table.table");
        System.out.println(body.get(2).child(0).getElementsByTag("a").text());
        name = body.get(2).child(0).getElementsByTag("a").text();
        return name;
    }
    }
