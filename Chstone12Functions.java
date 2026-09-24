import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Chstone12Functions {

    /**
     * 1글자짜리 문자열 앞에다가 글자 1개 넣기
     * @param st 1글자짜리 문자열
     * @param pad 앞에다가 붙일 글자
     * @return 문자열이 1글자라면 'pad'를 붙인 'st', 아니면 'st' 그대로.
     */
    public static String padString(String st, char pad) {
        return st.length() == 1 ? pad + st : st;
    }

    /**
     * 문자열 앞에다가 글자 넣기
     * @param st 글자가 들어갈 원본 문자열
     * @param pad 'st' 앞에 넣을 문자열
     * @param len 길이
     * @return 문자열의 길이가 'len'이 되도록 앞에다가 'pad'를 n개 넣은 'st'.
     * <br>만약 len - st.length() < 0이면 'st'를 그대로 돌려줌.
     */
    public static String padString(String st, String pad, int len) {
        int t = len - st.length();
        if(t < 0) return st;
        return pad.repeat(t) + st;
    }



    /**
     * 앞에 붙어 있는 글자들을 제거하는 메서드
     * @param st 원본 문자열
     * @param pad 제거할 글자
     * @return 앞에 붙어 있는 'pad'를 전부 지운 'st'.<br>예를 들어 st = "00001234"이고 pad = '0'이면 "1234"를 돌려줌.
     */
    public static String unpadString(String st, char pad) {
        int i = 0;
        for(; i < st.length(); i++) {
            if(st.charAt(i) != pad) break;
        }
        return st.substring(i);
    }


    /**
     * 앞에 붙어 있는 글자들을 제거하는 메서드
     * @param st 원본 문자열
     * @param pad 제거할 글자
     * @param len 리턴값의 최소 글자 수
     * @return 앞에 붙어 있는 'pad'를 전부 지운 'st'. 단, 리턴값의 길이가 'len'보다 짧아질 수 없음.<br>예를 들어 st = "00001234", pad = '0', len = 5면 "01234"를 돌려줌.<br>만약 st = "0001234", pad = '0', len = 100처럼 'len'이 st.length()보다 크다면 그냥 'st'를 그대로 돌려줌.
     */
    public static String unpadString(String st, char pad, int len) {
        int i = 0, stlen = st.length() + 1;
        for(; i < st.length(); i++) {
            stlen--;
            if(st.charAt(i) != pad || len >= stlen) break;
        }
        return st.substring(i);
    }



    /**
     * 문자열 끝에 붙어 있는 !, ?, ㅋ, ㅎ, ㅜ, ㅠ와 같은 감정 표현 문구를 분리해 주는 메서드.<br>감정 표현 문구 = !, ?, 띄어쓰기, ㅋ, ㅎ, 땀표, 마침표, ㅜ, ㅠ, 쉼표, >, <, ~, -, ^, @, ㅇ, ㄷ, ♡.
     * @param from 원본 문자열
     * @return 감정 표현 문구들을 분리한 String 배열.<br>예를 들어 입력이 "이예에에에에ㅋㅋㅋㅋㅎㅋㅋㅎㅋ"라면 0번째 인덱스가 "이예에에에에"이고 1번째 인덱스가 "ㅋㅋㅋㅋㅎㅋㅋㅎㅋ".
     */
    public static String[] splitLastMarkLetters(String from) {
        if (from == null || from.equals("")) return new String[]{"", ""};

        int length = from.length();
        int cutIndex = length;

        for (int i = length - 1; i >= 0; i--) {
            if (MARK_CHARS.contains(from.charAt(i))) cutIndex = i;
            else break;
        }

        return new String[]{from.substring(0, cutIndex), from.substring(cutIndex) };
    }

    private static final Set<Character> MARK_CHARS = Set.of('!', '?', ' ', 'ㅋ', 'ㅎ', ';', '.', 'ㅜ', 'ㅠ', ',', '>', '<', '~', '-', '^', '@', 'ㅇ', 'ㄷ', '♡');





    /**
     * YYYYMMDD를 자연스러운 문자열로 바꿈
     * @param yyyymmdd 년월일
     * @return YYYY년 MM월 DD일
     */
    public static String YYYYMMDDtoString(String yyyymmdd) {
        return yyyymmdd.substring(0, 4) + "년 " + unpadString(yyyymmdd.substring(4, 6), '0') + "월 " + unpadString(yyyymmdd.substring(6), '0') + "일";
    }


    /**
     * 특정 문자열에 또다른 어느 문자열이 몇 개 포함되어 있는지 세주는 메서드. ctrl + f로 개수를 세는 것과 똑같이 셈.
     * @param from 원본 문자열
     * @param countThis 셀 문자열
     * @return 'from'에 'countThis'가 몇 개 포함되어 있는지. <br>예를 들어 from = "12312333129123"이고 countThis = "123"이면 3 리턴,<br>from = "111111111", countThis = "111"이면 3 리턴.
     */
    public static int countString(String from, String countThis) {
        int r = 0, countThisLen = countThis.length();
        int dif = from.length() - countThisLen + 1;
        for(int i = 0; i < dif; i++) {
            if(from.substring(i, countThisLen + i).equals(countThis)) {
                r++;
                i += countThisLen - 1;
            }
        }
        return r;
    }


    /**
     * 파일에다가 내용 덮어쓰기
     * @param write 이걸 덮어쓸거임
     * @param targetPath 여기에다가
     * @return 파일이 존재하면 true, 아니면 false
     */
    public static boolean writeThisToFile(String write, String targetPath) {
        try {

            BufferedWriter w = new BufferedWriter(new FileWriter(targetPath));
            w.write(write);
            w.close();
            return true;

        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 파일에다가 내용 추가하기
     * @param addition 맨아랫줄에다가 추가할 내용
     * @param targetPath 파일 경로
     * @return 파일이 존재하면 true, 아니면 false
     */
    public static boolean addThisToFile(String addition, String targetPath) {
        try {

            String t = Files.readString(Path.of(targetPath)).trim();
            BufferedWriter w = new BufferedWriter(new FileWriter(targetPath));
            w.write(t + "\n" + addition);
            w.close();
            return true;

        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * try catch 없이 파일 읽기
     * @param targetPath 읽을 파일 경로
     * @return 파일이 존재하면 파일의 내용, 아니라면 예외 메시지
     */
    public static String readFile(String targetPath) {
        try {

            return Files.readString(Path.of(targetPath));

        } catch (Exception e) {
            return e.toString();
        }
    }


    /**
     * try catch 없이 파일 읽기
     * @param targetPath 읽을 파일 경로
     * @param alt 파일이 없다면 이게 튀어나옴
     * @return 파일이 존재하면 파일의 내용, 아니라면 alt
     */
    public static String readFileOrDefault(String targetPath, String alt) {
        try {

            return Files.readString(Path.of(targetPath));

        } catch (Exception e) {
            return alt;
        }
    }

    /**
     * try catch 없이 파일을 줄 단위로 List{@literal <String>}으로 읽기
     * @param targetPath 읽을 파일 경로
     * @return 줄 단위로 읽은 파일, 파일이 없다면 List.of("X", e.toString())
     */
    public static List<String> readFileAsList(String targetPath) {
        try {

            return Files.readAllLines(Path.of(targetPath));

        } catch (Exception e) {
            return List.of("X", e.toString());
        }
    }


    /**
     * 파일에서 랜덤한 한 줄 출력하기
     * @param targetPath 파일
     * @return 존재하는 파일이면 그 파일의 랜덤한 한 줄의 내용, 아니면 예외 메시지
     */
    public static String pickRandomLineFromFile(String targetPath) {
        try {
            List<String> k = Files.readAllLines(Path.of(targetPath));
            int r = (int) (Math.random() * k.size());
            return k.get(r);
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * 파일에서 랜덤한 count개의 줄을 리스트로 반환하는 메서드
     * @param targetPath 읽을 파일
     * @param count 읽을 줄 개수
     * @param allowDupl 중복된 줄을 허용할지 (중복된 값이 아님; targetPath 파일 자체에 중복된 내용을 가진 2+개의 줄이 있으면 중복된 내용은 튀어나올 수 있음)
     * @return 존재하는 파일이면 .size()의 값이 count인, targetPath가 가리키는 파일에서 랜덤으로 뽑은 count개의 줄의 내용 (수정 가능),
     * <br>존재하지 않는 파일이면 List.of("X", e.toString()),
     * <br>targetPath가 가리키는 파일의 줄 수가 count보다 작으면서 allowDupl == false면 List.of("X", "무한루프!!")
     * <br> count < 0이면 List.of("X", "음수 개를 어떻게 읽냐"),
     * <br> 파일이 비어 있고 count != 0이면 List.of("X", "빈 리스트거나 읽을 원소가 없습니다.").
     */
    public static List<String> pickRandomLinesFromFile(String targetPath, int count, boolean allowDupl) {
        try {

            List<String> k = Files.readAllLines(Path.of(targetPath));
            return pickRandomLines(k, count, allowDupl);

        } catch (Exception e) {
            return List.of("X", e.toString());
        }
    }


    /**
     * 리스트에서 랜덤한 count개의 원소를 리스트로 반환하는 메서드
     * @param k 뽑아먹을 리스트
     * @param count 읽을 줄 개수
     * @param allowDupl 중복된 index를 허용할지 (중복된 값이 아님; 리스트 자체에 중복된 값을 가진 2+개의 원소가 있으면 false여도 중복된 값은 튀어나올 수 있음)
     * @return .size()의 값이 count인, k에서 랜덤으로 뽑은 count개의 원소들의 값 (수정 가능),
     * <br> k.size() < count이면서 allowDupl == false면 List.of("X", "무한루프!!")
     * <br> count < 0이면 List.of("X", "음수 개를 어떻게 읽냐"),
     * <br> 리스트가 비어 있고 count != 0이면 List.of("X", "빈 리스트거나 읽을 원소가 없습니다.").
     */
    public static List<String> pickRandomLines(List<String> k, int count, boolean allowDupl) {

        if(count < 0) return List.of("X", "음수 개를 어떻게 읽냐");
        else if(count == 1) return new ArrayList<>(Collections.singletonList(k.get((int) (Math.random() * k.size()))));
        
        List<String> returnThis = new ArrayList<>();
        int[] randomNumbers = new int[count];
        int max = k.size();
        if (max == 0 && count != 0) return List.of("X", "빈 리스트거나 읽을 원소가 없습니다.");
        if(allowDupl) {
            for(int i = 0; i < count; i++) {
                randomNumbers[i] = (int) (Math.random() * max);
            }
        }
        else {
            if(max < count) return List.of("X", "무한루프!!");
            else if(max == count) {
                Collections.shuffle(k);
                return k;
            }
            else if(max / 4 < count) {
                int removalCount = max - count;
                Set<Integer> removals = new HashSet<>();
                while (removals.size() < removalCount) {
                    removals.add((int) (Math.random() * max));
                }
                List<String> filtered = new ArrayList<>(max - removalCount);
                for(int i = 0; i < max; i++) {
                    if(!removals.contains(i)) {
                        filtered.add(k.get(i));
                    }
                }
                Collections.shuffle(filtered);
                return filtered;
            }
            if(max < 128) {
                List<Integer> list = new ArrayList<>();
                for(int i = 0; i < max; i++) list.add(i);
                Collections.shuffle(list);
                for(int i = 0; i < count; i++) {
                    randomNumbers[i] = list.get(i);
                }
            }
            else {
                boolean dupl;
                for(int i = 0; i < count; i++) {
                    int r = (int) (Math.random() * max);
                    dupl = false;
                    for(int j = 0; j < i; j++) {
                        if(randomNumbers[j] == r) {
                            dupl = true;
                            break;
                        }
                    }
                    if(dupl) i--;
                    else randomNumbers[i] = r;
                }
            }
        }
        for(int i : randomNumbers) {
            returnThis.add(k.get(i));
        }
        return returnThis;

    }



    /**
     * 문자열 리스트의 각 원소 사이사이에 separator를 집어넣는 메서드
     * @param list 문자열 리스트
     * @param separator 원소 사이사이에 넣을 문자열. 띄어쓰기도 넣어야 됨
     * @return separator를 끼워넣은 문자열. 만약 list가 비어 있다면 비어 있는 문자열 (separator 안 붙음).
     */
    public static String convertToString(List<String> list, String separator) {
        StringBuilder sb = new StringBuilder();
        if(list.isEmpty() || (list.size() == 1 && list.get(0).equals(""))) return "";
        for(String s : list) {
            sb.append(s).append(separator);
        }
        sb.setLength(sb.length()-separator.length());
        return sb.toString();
    }


    /**
     * 문자열 배열의 각 원소 사이사이에 separator를 집어넣는 메서드
     * @param list 문자열 배열
     * @param separator 원소 사이사이에 넣을 문자열. 띄어쓰기도 넣어야 됨
     * @return separator를 끼워넣은 문자열.
     */
    public static String convertToString(String[] list, String separator) {
        StringBuilder sb = new StringBuilder();
        for(String s : list) {
            sb.append(s).append(separator);
        }
        sb.setLength(sb.length()-separator.length());
        return sb.toString();
    }


    /**
     * Map을 파일에다가 저장하기 (나중에 readFileAsMap으로 읽을 수 있음)
     * @param map 저장할 맵
     * @param path 저장될 파일 위치
     * @param separator 나중에 readFileAsMap 쓸 때 필요한 키와 값 사이 구분자. 정규식으로 치환될 수 있는 문자 쓰면 안 됨
     */
    public static <K, V> void writeThisMapToFile(Map<K, V> map, String path, String separator) {
        Set<K> set = map.keySet();
        StringBuilder sb = new StringBuilder();
        for(K k : set) {
            V value = map.get(k);
            String stringKey = k.toString();
            String stringValue = value.toString();
            sb.append(stringKey).append(separator).append(stringValue).append("\n");
        }
        writeThisToFile(sb.toString(), path);
    }

    /**
     * writeThisMapToFile로 저장한 맵을 다시 읽기
     * @param path 맵이 저장된 위치
     * @param kType 저장할 때 썼던 키의 타입 (String, Integer, Long, Double, Boolean 중 하나)
     * @param vType 저장할 때 썼던 값의 타입 (String, Integer, Long, Double, Boolean 중 하나)
     * @return 파일에 저장된 맵
     */
    public static <K, V> Map<K, V> readFileAsMap(String path, Class<K> kType, Class<V> vType, String separator) {
        List<String> file = readFileAsList(path);
        Map<K, V> map = new HashMap<>();

        for (String s : file) {
            if (s.isEmpty()) continue;
            String[] t = s.split(separator, 2);
            if (t.length < 2) continue;

            K key = parse(t[0], kType);
            V value = parse(t[1], vType);
            map.put(key, value);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static <T> T parse(String s, Class<T> type) {
        if (type == String.class)    return (T) s;
        if (type == Integer.class)   return (T) Integer.valueOf(s);
        if (type == Long.class)      return (T) Long.valueOf(s);
        if (type == Double.class)    return (T) Double.valueOf(s);
        if (type == Boolean.class)   return (T) Boolean.valueOf(s);
        throw new IllegalArgumentException("지원하지 않는 타입: " + type.getName());
    }


    /**
     * writeThisMapToFile로 저장한 {@literal Map<K, List<String>>}을 불러오기
     * @param path 파일 위치
     * @param keySeparator 키와 값 사이를 구분할 때 썼던 구분자 그대로
     * @param valueSeparator 값의 원소 사이를 구분할 때 썼던 구분자 그대로
     * @param k 키의 타입
     * @return path에 저장된 맵
     */
    public static <K> Map<K, List<String>> readFileAsMap(String path, String keySeparator, String valueSeparator, Class<K> k) {
        List<String> file = readFileAsList(path);
        Map<K, List<String>> map = new HashMap<>();
        for(String s : file) {
            String[] t = s.split(keySeparator);
            if(t.length == 1 || t[1].equals("")) continue;
            List<String> tl = new ArrayList<>(List.of(t[1].split(valueSeparator)));
            map.put(parse(t[0], k), tl);
        }
        return map;

    }

    /**
     * {@literal Map<K, List<String>>}을 파일에 저장하기. toString이 제대로 안되는 타입 쓰면 이상하게 저장됨 (readFileAsMap으로 읽을 수 있음)
     * @param map 저장할 맵
     * @param path 저장할 위치
     * @param keySeparator 키와 값 사이를 구분할 구분자 (후에 다시 읽을 때 똑같이 써야 됨)
     * @param valueSeparator 값의 원소 사이를 구분할 구분자 (마찬가지)
     */
    public static <K> void writeThisMapToFile(Map<K, List<String>> map, String path, String keySeparator, String valueSeparator) {
        StringBuilder sb = new StringBuilder();
        for(K s : map.keySet()) {
            String t = convertToString(map.get(s), valueSeparator);
            sb.append(s.toString()).append(keySeparator).append(t).append("\n");
        }
        writeThisToFile(sb.toString(), path);
    }


    /**
     * 구글번역기에다가 input 번역 돌리기. 이거 비동기 처리 아니라서 좀 걸림 + 사이트에서 번역하는 것보다 성능 떨어짐
     * @param input 번역할 문자열
     * @param languages 언어들. 예를 들어 {"ko", "en"}을 집어넣으면 한국어를 영어로 번역함. {"ko", "en", "ja"}를 쓰면 한국어를 영어로 번역하고, 그 영어를 또 일본어로 번역함. (계속 이을 수 있지만 길어질수록 시간 오래 걸림)
     * @param showProgress 번역 과정을 보여줄지 정하는 변수. -1로 쓰면 절대 안 보여줌. 자연수를 쓰면 리턴값이 해당 글자 수 이상인 경우 안 보여줌.
     * @return 0번 인덱스에는 변역 결과, 1번 인덱스에는 번역 과정이 포함된 길이가 2인 문자열 배열. showProgress 값과 과정의 글자 수에 따라 1번 인덱스에는 비어 있는 문자열이 들어갈 수도 있음
     */
    public static String[] googleTranslate(String input, String[] languages, int showProgress) {
        StringBuilder finalOutput = new StringBuilder();
        StringBuilder progress = new StringBuilder();
        int languageCount = languages.length - 1;


        if (languages.length == 0) {
            return new String[2];
        }


        String translating = input;
        progress.append(translating).append(" -> ");
        for (int i = 0; i < languageCount; i++) {
            String fromLan;
            String toLan = languages[i + 1];
            fromLan = languages[i];

            try {
                URL url = new URL("https://clients5.google.com/translate_a/t?client=dict-chrome-ex&sl=" + fromLan + "&tl=" + toLan + "&q=" + URLEncoder.encode(translating, StandardCharsets.UTF_8));
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                translating = new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception ignored) {}
            translating = translating.substring(2, translating.length() - 2);

            if (i == languageCount - 1) {
                progress.append(translating);
                finalOutput.append(translating);
            } else progress.append(translating).append(" -> ");
        }

        return showProgress >= progress.length() ? new String[]{finalOutput.toString(), progress.toString()} : new String[]{finalOutput.toString(), ""};
    }


    /**
     * 입력된 폴더 안에 들어 있는 모든 파일들의 경로 반환하는 메서드
     * @param path 폴더 경로
     * @return 존재하지 않는 폴더라면 null, 존재하는 폴더라면 해당 폴더 안에 들어 있는 모든 파일들의 경로.
     */
    public static List<String> getAllFilePaths(String path) {
        Path rootPath = Paths.get(path);

        try (var stream = Files.walk(rootPath)) {

            return stream.filter(Files::isRegularFile)
                    .map(k -> k.toAbsolutePath().toString())
                    .collect(Collectors.toList());


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 입력된 폴더 안에 들어 있는 모든 파일들의 경로 반환하는 메서드
     * @param path 폴더 경로
     * @param p 필터링하기; 얘가 없어지는 게 아니라 얘만 남음
     * @return 존재하지 않는 폴더라면 null, 존재하는 폴더라면 해당 폴더 안에 들어 있는 모든 필터링된 파일들의 경로.
     */
    public static List<String> getAllFilePaths(String path, Predicate<String> p) {
        Path rootPath = Paths.get(path);

        try (var stream = Files.walk(rootPath)) {

            return stream.filter(Files::isRegularFile)
                    .map(k -> k.toAbsolutePath().toString())
                    .filter(p)
                    .collect(Collectors.toList());


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * SHA 256 암호화
     * @param text 암호화할 텍스트
     * @return 암호화된 텍스트
     */
    public static String encrypt(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());
            byte[] byteData = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : byteData) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 여기 올 일 없음
        return "";
    }

    /**
     * 배열에 어떤 원소가 포함돼 있는지 리턴
     * @param ar 배열
     * @param find 찾을 문자열
     * @return 있으면 true, 없으면 false.
     */
    public static boolean containsArray(String[] ar, String find) {
        for(String s : ar) {
            if(s.equals(find)) return true;
        }
        return false;
    }


    /**
     * 배열에 어떤 원소가 어디에 있는지 리턴
     * @param ar 배열
     * @param find 찾을 문자열
     * @return find가 ar의 몇 번째 인덱스에 있는지, 없으면 -1. 여러개 있으면 가장 먼저 찾은 것의 위치만 (0부터 찾음)
     */
    public static int containsArrayWhere(String[] ar, String find) {
        for(int i = 0; i < ar.length; i++) {
            if(ar[i].equals(find)) return i;
        }
        return -1;
    }


    /**
     * 컬렉션에서 어떤 원소가 대소문자 무시하고 포함돼있는지 리턴
     * @param c 컬렉션
     * @param find 찾을 문자열
     * @return 대소문자만 다른 두 문자열은 같다고 취급했을 때 c 안에 find가 들어 있다면 true, 아니면 false. (그냥 equalsIgnoreCase 여러번 하는 거임)
     */
    public static <E> boolean containsIgnoreCase(Collection<E> c, String find) {
        for(E e : c) {
            if(String.valueOf(e).equalsIgnoreCase(find)) return true;
        }
        return false;
    }

    
    public static String getFromMapIgnoreCase(Map<String, String> m, String getThis) {

        for (Map.Entry<String, String> entry : m.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (k.equalsIgnoreCase(getThis)) {
                return v;
            }
        }

        return null;
    }



}
