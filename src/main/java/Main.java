import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class Main {
    static String resultFolderName = String.format("./results/%s/", System.currentTimeMillis());
    static String bookName;


    public static void main(String[] args) throws IOException {
//        File directory = new File("./");
//        System.out.println(directory.getAbsolutePath());

//        считать html файл
//        File input = new File("./html/test.html");
//        File input = new File("./html/Pro Git.html");
        File input = new File("./html/sp.html");
        Document document = Jsoup.parse(input, "UTF-8");
        // извлечь название книги
        bookName = extractBookName(
            document.body().getElementsByTag("h1").text()
        );

        //создаем новую директорию для изображений и заметок
        File resultDir = new File(resultFolderName);
        resultDir.mkdir();

        Elements divs = document.body().getElementsByTag("div");
        for (Element div : divs) {
            if (!div.hasAttr("id")) {
                continue;
            }

            String divId = div.id();

            StringBuilder note = new StringBuilder();
            // добавляем в заметку md ссылку на книгу
            note.append(String.format("[[%s]]\n\n", bookName));

            Element textDiv = div.getElementsByClass("bm-text").first();
            if (textDiv != null) {
                String noteText = textDiv.text();
                if ((noteText.equals("Bookmark") || noteText.equals("Карандаш"))) {
                    continue;
                }
                note.append(String.format("%s\n\n", noteText));
            }

            Element comment = div.getElementsByClass("bm-note").first();
            if (comment != null) {
                note.append(String.format("Комментарий %s\n\n", comment.text()));
            }

            Element imageDiv = div.getElementsByClass("bm-image").first();
            if (imageDiv != null) {
                String savedImageName = saveImageAndGetName(imageDiv);
                note.append(String.format("![[%s]]\n\n", savedImageName));
            }

            Element pageNumber = div.getElementsByClass("bm-page").first();
            if (pageNumber != null) {
                note.append(String.format("Страница: %s\n\n", pageNumber.text()));
            }


            //Создание файла заметки
//            Path path = Paths.get(String.format("%s%s--%s.md", resultFolderName, bookName, System.currentTimeMillis()));
            Path path = Paths.get(String.format("%s%s.md", resultFolderName,  System.currentTimeMillis()));
            byte[] strToBytes = note.toString().getBytes();
            Files.write(path, strToBytes);

            System.out.println();
        }

    }

    private static String saveImageAndGetName(Element imageDiv) throws IOException {
        Element img = imageDiv.select("img").first();
        assert img != null;
        String imageBase64string = img.attr("src");

        String name = String.format("%s_%s.png", bookName, System.currentTimeMillis());

        String partSeparator = ",";
        if (imageBase64string.contains(partSeparator)) {
            String encodedImg = imageBase64string.split(partSeparator)[1];
            byte[] decodedImg = Base64.getDecoder().decode(encodedImg.getBytes(StandardCharsets.UTF_8));
            Path destinationFile = Paths.get(resultFolderName, name);
            Files.write(destinationFile, decodedImg);
        }

        return name;
    }

    private static String extractBookName(String headerText) {
//        "2022-08-30 13:28:10 - " - 22 символа
        return headerText.substring(22);
    }
}
