package io.ankoaaaa.mangaRecommendApi.constants;

import java.util.List;

public class PromptConstants {
	// プライベートコンストラクタでインスタンス化を防ぐ
	private PromptConstants() {
	}

	// --- システムプロンプト (AIの役割定義) ---
	public static final String SYSTEM_PROMPT = "あなたは、日本の漫画に非常に詳しい、フレンドリーな漫画ソムリエです。" +
			"ユーザーが入力した漫画の情報を元に、的確な推薦を行ってください。" +
			"事実と異なること、不確かなことは絶対に述べないでください。";

	// --- Few-Shotプロンプティング (AIへの手本) ---
	public static final String EXAMPLE_INPUT = "入力: " +
			"漫画のタイトル: 1日外出録ハンチョウ" +
			"好きな理由: 限られた状況で最大限楽しむ工夫と、独特の飯テロ。";

	public static final String EXAMPLE_OUTPUT = "出力: " +
			"{" +
			"\"introduction\":\"『1日外出録ハンチョウ』、面白いですよね！あの、限られた状況の中でいかに「食」と「娯楽」を最大限に楽しむかという創意工夫と、大槻班長の独特な哲学がたまらない魅力です。\","
			+
			"\"categories\":[" +
			"{\"categoryTitle\":\"「食」がテーマの漫画\"," +
			"\"recommendations\":[" +
			"{\"title\":\"きのう何食べた？\",\"description\":\"弁護士と美容師の男性カップルの日常を、食生活を中心に描く物語。\",\"interpretation\":\"『ハンチョウ』の「丁寧な食事」の描写が好きなら、心温まる家庭料理と人間ドラマに癒やされること間違いなしです。\"},"
			+
			"{\"title\":\"ダンジョン飯\",\"description\":\"ダンジョンの奥深くで、倒したモンスターを調理して食べる異色のグルメファンタジー。\",\"interpretation\":\"『ハンチョウ』の「限られた状況での創意工夫」という点に、ファンタジーとグルメを掛け合わせた面白さが刺さるはずです。\"}"
			+
			"]}" +
			// ... 他のカテゴリも同様に続く ...
			"]" +
			"}";

	// --- ユーザーへの指示 ---
	public static final String USER_INSTRUCTION = "上記の手本を参考に、以下の入力に対して、指定されたJSONフォーマットで回答を生成してください。3ジャンルで2作品ごと教えてください。どちらかというとマイナーよりなほうがうれしいです。";

	// --- JSONフォーマット定義 ---
	public static final String FORMAT_INSTRUCTION = "JSONフォーマット: {\"introduction\": \"入力された漫画への共感コメント(100～200字)\", \"categories\": [{\"categoryTitle\": \"カテゴリ名\", \"recommendations\": [{\"title\": \"漫画のタイトル\", \"description\": \"漫画の簡単な説明(50字程度)\", \"interpretation\": \"入力された漫画との共通点や、なぜおすすめかの解説(50字程度)\"}]}]}";

	/**
	 * ユーザーの入力内容から、最終的なプロンプトを組み立てて返すメソッド
	 * @param titles 好きな漫画のタイトルのリスト
	 * @param reason 好きな理由
	 * @return AIに送信する最終的なプロンプト文字列
	 */
	public static String buildFinalPrompt(List<String> titles, String reason) {
		String formattedTitles = String.join("、", titles);
		String reasonText = (reason != null && !reason.isEmpty()) ? reason : "特になし";

		String userInput = "入力:\n" +
				"漫画のタイトル: " + formattedTitles + "\n" +
				"好きな理由: " + reasonText;

		return SYSTEM_PROMPT + "\n\n" +
				"--- (手本ここから) ---\n" +
				EXAMPLE_INPUT + "\n" +
				EXAMPLE_OUTPUT + "\n" +
				"--- (手本ここまで) ---\n\n" +
				USER_INSTRUCTION + "\n\n" +
				userInput + "\n\n" +
				"出力:\n" +
				FORMAT_INSTRUCTION;
	}

}
