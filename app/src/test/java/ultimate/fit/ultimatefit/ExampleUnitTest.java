package ultimate.fit.ultimatefit;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        System.out.print(test(2, "1A 2F 1C"));
    }
    public int test(int N, String S){
        S = S.trim().toUpperCase();
       if(S.length() == 0) return N*3;
       String[] seats= S.trim().split(" ");
       int result = 0;
       int[][] marks = new int[3][N];
       for(int i=0;i<seats.length;i++){
           char character = seats[i].charAt(seats[i].length()-1);
           int number = Integer.valueOf(seats[i].substring(0,seats[i].length()-1));
           if(character>=65 && character <= 67) marks[0][number] = 2;
           else if(character >=72) marks[2][number] = 2;
           else{
               if(marks[1][number] == 0) marks[1][number] = 1;
               else marks[1][number] = 2;
           }
       }
       for(int i=0;i<3;i++){
           for(int j=0;j<N;j++){
               if(marks[i][j]<2) result++;
           }
       }
        return result;
    }
}