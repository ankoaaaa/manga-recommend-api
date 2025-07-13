package io.ankoaaaa.mangaRecommendApi.constants;

public class PromptConstants {
	// プライベートコンストラクタでインスタンス化を防ぐ
	private PromptConstants() {
	}

	public static final String BASE = "以下の漫画が好きです。 {titles} "
			+ "特に「{reason}」という点に魅力を感じています。"
			+ "この人が好きそうな漫画を2つ推薦してください。";

	public static final String INSTRUCTION = "推薦理由は50字程度、あなたがこの漫画を推薦する独自の解釈を30字程度で、"
			+ "必ず以下のJSONフォーマットのみで回答してください。他のテキストは一切含めないでください。";

	public static final String FORMAT = "フォーマット: {\"recommendations\": [{\"title\": \"漫画のタイトル\", \"description\": \"漫画の簡単な説明\", \"interpretation\": \"なぜおすすめだと思ったかの解説\"}]}";

}
