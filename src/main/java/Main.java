import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        File directory = new File("./");
//        System.out.println(directory.getAbsolutePath());

//        считать html файл
        File input = new File("./html/test.html");
        Document document = Jsoup.parse(input, "UTF-8");
        // извлечь название книги
        String bookName = extractBookName(
            document.body().getElementsByTag("h1").text()
        );

        Elements divs = document.body().getElementsByTag("div");
        for (Element div : divs) {
            if (!div.hasAttr("id")) {
                continue;
            }

            String divId = div.id();

            String note = String.format("[[%s]]\n\n", bookName);




        }
        System.out.println();

    }

    private static String extractBookName(String headerText) {
//        "2022-08-30 13:28:10 - " - 22 символа
        return headerText.substring(22);
    }
}
