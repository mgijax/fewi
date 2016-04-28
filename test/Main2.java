
public class Main2 {

	public static void main(String[] args) {
		String s = "1234567890-=qwertyuiop[]\\asdfghjkl;'zxc vbnm,./!@#$%^&*()_+QWERTYUIOP{}|ASDFGHJKL:\"ZXCVBNM<>?";
		for(String s1: getTokens(s)) {
			System.out.println(s1);
		}
	}
	
	public static String[] getTokens(String input) {
		input = input.replaceAll("[^\\w^\\*\\^]+", " ");
		input = input.replaceAll("\\^", " ");
		input = input.replaceAll("_", " ");
		input = input.replaceAll("\\s+", " ").trim();
		return input.split(" ");
	}

}
