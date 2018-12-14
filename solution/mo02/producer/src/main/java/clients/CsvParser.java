package clients;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CsvParser{
    public List<Station> parse(String fileName){
        File file= new File(fileName);

        List<Station> lines = new ArrayList<>();
        Scanner inputStream;

        try{
            inputStream = new Scanner(file);

            while(inputStream.hasNext()){
                String line= inputStream.next();
                Station station = new Station(line);
                lines.add(station);
            }

            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
