// Generated from D:/sjtu(3)/±‡“Î∆˜/Mx/src/Parser\Mx.g4 by ANTLR 4.9
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MxLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, Int=2, If=3, Else=4, Return=5, While=6, Break=7, Continue=8, LeftParen=9, 
		RightParen=10, LeftBracket=11, RightBracket=12, LeftBrace=13, RightBrace=14, 
		Less=15, LessEqual=16, Greater=17, GreaterEqual=18, LeftShift=19, RightShift=20, 
		Plus=21, Minus=22, PlusPlus=23, MinusMinus=24, Star=25, Div=26, Mod=27, 
		And=28, Or=29, AndAnd=30, OrOr=31, Caret=32, Not=33, Tilde=34, Question=35, 
		Colon=36, Semi=37, Comma=38, Assign=39, Equal=40, NotEqual=41, Identifier=42, 
		DecimalInteger=43, Whitespace=44, Newline=45, BlockComment=46, LineComment=47;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "Int", "If", "Else", "Return", "While", "Break", "Continue", 
			"LeftParen", "RightParen", "LeftBracket", "RightBracket", "LeftBrace", 
			"RightBrace", "Less", "LessEqual", "Greater", "GreaterEqual", "LeftShift", 
			"RightShift", "Plus", "Minus", "PlusPlus", "MinusMinus", "Star", "Div", 
			"Mod", "And", "Or", "AndAnd", "OrOr", "Caret", "Not", "Tilde", "Question", 
			"Colon", "Semi", "Comma", "Assign", "Equal", "NotEqual", "Identifier", 
			"DecimalInteger", "Whitespace", "Newline", "BlockComment", "LineComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'int main()'", "'int'", "'if'", "'else'", "'return'", "'while'", 
			"'break'", "'continue'", "'('", "')'", "'['", "']'", "'{'", "'}'", "'<'", 
			"'<='", "'>'", "'>='", "'<<'", "'>>'", "'+'", "'-'", "'++'", "'--'", 
			"'*'", "'/'", "'%'", "'&'", "'|'", "'&&'", "'||'", "'^'", "'!'", "'~'", 
			"'?'", "':'", "';'", "','", "'='", "'=='", "'!='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "Int", "If", "Else", "Return", "While", "Break", "Continue", 
			"LeftParen", "RightParen", "LeftBracket", "RightBracket", "LeftBrace", 
			"RightBrace", "Less", "LessEqual", "Greater", "GreaterEqual", "LeftShift", 
			"RightShift", "Plus", "Minus", "PlusPlus", "MinusMinus", "Star", "Div", 
			"Mod", "And", "Or", "AndAnd", "OrOr", "Caret", "Not", "Tilde", "Question", 
			"Colon", "Semi", "Comma", "Assign", "Equal", "NotEqual", "Identifier", 
			"DecimalInteger", "Whitespace", "Newline", "BlockComment", "LineComment"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public MxLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Mx.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\61\u011a\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\30\3\31\3\31"+
		"\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3\37"+
		"\3 \3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3"+
		")\3*\3*\3*\3+\3+\7+\u00e3\n+\f+\16+\u00e6\13+\3,\3,\7,\u00ea\n,\f,\16"+
		",\u00ed\13,\3,\5,\u00f0\n,\3-\6-\u00f3\n-\r-\16-\u00f4\3-\3-\3.\3.\5."+
		"\u00fb\n.\3.\5.\u00fe\n.\3.\3.\3/\3/\3/\3/\7/\u0106\n/\f/\16/\u0109\13"+
		"/\3/\3/\3/\3/\3/\3\60\3\60\3\60\3\60\7\60\u0114\n\60\f\60\16\60\u0117"+
		"\13\60\3\60\3\60\3\u0107\2\61\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13"+
		"\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61"+
		"\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61"+
		"\3\2\b\4\2C\\c|\6\2\62;C\\aac|\3\2\63;\3\2\62;\4\2\13\13\"\"\4\2\f\f\17"+
		"\17\2\u0121\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2"+
		"\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2"+
		"\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S"+
		"\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2"+
		"\2\2\3a\3\2\2\2\5l\3\2\2\2\7p\3\2\2\2\ts\3\2\2\2\13x\3\2\2\2\r\177\3\2"+
		"\2\2\17\u0085\3\2\2\2\21\u008b\3\2\2\2\23\u0094\3\2\2\2\25\u0096\3\2\2"+
		"\2\27\u0098\3\2\2\2\31\u009a\3\2\2\2\33\u009c\3\2\2\2\35\u009e\3\2\2\2"+
		"\37\u00a0\3\2\2\2!\u00a2\3\2\2\2#\u00a5\3\2\2\2%\u00a7\3\2\2\2\'\u00aa"+
		"\3\2\2\2)\u00ad\3\2\2\2+\u00b0\3\2\2\2-\u00b2\3\2\2\2/\u00b4\3\2\2\2\61"+
		"\u00b7\3\2\2\2\63\u00ba\3\2\2\2\65\u00bc\3\2\2\2\67\u00be\3\2\2\29\u00c0"+
		"\3\2\2\2;\u00c2\3\2\2\2=\u00c4\3\2\2\2?\u00c7\3\2\2\2A\u00ca\3\2\2\2C"+
		"\u00cc\3\2\2\2E\u00ce\3\2\2\2G\u00d0\3\2\2\2I\u00d2\3\2\2\2K\u00d4\3\2"+
		"\2\2M\u00d6\3\2\2\2O\u00d8\3\2\2\2Q\u00da\3\2\2\2S\u00dd\3\2\2\2U\u00e0"+
		"\3\2\2\2W\u00ef\3\2\2\2Y\u00f2\3\2\2\2[\u00fd\3\2\2\2]\u0101\3\2\2\2_"+
		"\u010f\3\2\2\2ab\7k\2\2bc\7p\2\2cd\7v\2\2de\7\"\2\2ef\7o\2\2fg\7c\2\2"+
		"gh\7k\2\2hi\7p\2\2ij\7*\2\2jk\7+\2\2k\4\3\2\2\2lm\7k\2\2mn\7p\2\2no\7"+
		"v\2\2o\6\3\2\2\2pq\7k\2\2qr\7h\2\2r\b\3\2\2\2st\7g\2\2tu\7n\2\2uv\7u\2"+
		"\2vw\7g\2\2w\n\3\2\2\2xy\7t\2\2yz\7g\2\2z{\7v\2\2{|\7w\2\2|}\7t\2\2}~"+
		"\7p\2\2~\f\3\2\2\2\177\u0080\7y\2\2\u0080\u0081\7j\2\2\u0081\u0082\7k"+
		"\2\2\u0082\u0083\7n\2\2\u0083\u0084\7g\2\2\u0084\16\3\2\2\2\u0085\u0086"+
		"\7d\2\2\u0086\u0087\7t\2\2\u0087\u0088\7g\2\2\u0088\u0089\7c\2\2\u0089"+
		"\u008a\7m\2\2\u008a\20\3\2\2\2\u008b\u008c\7e\2\2\u008c\u008d\7q\2\2\u008d"+
		"\u008e\7p\2\2\u008e\u008f\7v\2\2\u008f\u0090\7k\2\2\u0090\u0091\7p\2\2"+
		"\u0091\u0092\7w\2\2\u0092\u0093\7g\2\2\u0093\22\3\2\2\2\u0094\u0095\7"+
		"*\2\2\u0095\24\3\2\2\2\u0096\u0097\7+\2\2\u0097\26\3\2\2\2\u0098\u0099"+
		"\7]\2\2\u0099\30\3\2\2\2\u009a\u009b\7_\2\2\u009b\32\3\2\2\2\u009c\u009d"+
		"\7}\2\2\u009d\34\3\2\2\2\u009e\u009f\7\177\2\2\u009f\36\3\2\2\2\u00a0"+
		"\u00a1\7>\2\2\u00a1 \3\2\2\2\u00a2\u00a3\7>\2\2\u00a3\u00a4\7?\2\2\u00a4"+
		"\"\3\2\2\2\u00a5\u00a6\7@\2\2\u00a6$\3\2\2\2\u00a7\u00a8\7@\2\2\u00a8"+
		"\u00a9\7?\2\2\u00a9&\3\2\2\2\u00aa\u00ab\7>\2\2\u00ab\u00ac\7>\2\2\u00ac"+
		"(\3\2\2\2\u00ad\u00ae\7@\2\2\u00ae\u00af\7@\2\2\u00af*\3\2\2\2\u00b0\u00b1"+
		"\7-\2\2\u00b1,\3\2\2\2\u00b2\u00b3\7/\2\2\u00b3.\3\2\2\2\u00b4\u00b5\7"+
		"-\2\2\u00b5\u00b6\7-\2\2\u00b6\60\3\2\2\2\u00b7\u00b8\7/\2\2\u00b8\u00b9"+
		"\7/\2\2\u00b9\62\3\2\2\2\u00ba\u00bb\7,\2\2\u00bb\64\3\2\2\2\u00bc\u00bd"+
		"\7\61\2\2\u00bd\66\3\2\2\2\u00be\u00bf\7\'\2\2\u00bf8\3\2\2\2\u00c0\u00c1"+
		"\7(\2\2\u00c1:\3\2\2\2\u00c2\u00c3\7~\2\2\u00c3<\3\2\2\2\u00c4\u00c5\7"+
		"(\2\2\u00c5\u00c6\7(\2\2\u00c6>\3\2\2\2\u00c7\u00c8\7~\2\2\u00c8\u00c9"+
		"\7~\2\2\u00c9@\3\2\2\2\u00ca\u00cb\7`\2\2\u00cbB\3\2\2\2\u00cc\u00cd\7"+
		"#\2\2\u00cdD\3\2\2\2\u00ce\u00cf\7\u0080\2\2\u00cfF\3\2\2\2\u00d0\u00d1"+
		"\7A\2\2\u00d1H\3\2\2\2\u00d2\u00d3\7<\2\2\u00d3J\3\2\2\2\u00d4\u00d5\7"+
		"=\2\2\u00d5L\3\2\2\2\u00d6\u00d7\7.\2\2\u00d7N\3\2\2\2\u00d8\u00d9\7?"+
		"\2\2\u00d9P\3\2\2\2\u00da\u00db\7?\2\2\u00db\u00dc\7?\2\2\u00dcR\3\2\2"+
		"\2\u00dd\u00de\7#\2\2\u00de\u00df\7?\2\2\u00dfT\3\2\2\2\u00e0\u00e4\t"+
		"\2\2\2\u00e1\u00e3\t\3\2\2\u00e2\u00e1\3\2\2\2\u00e3\u00e6\3\2\2\2\u00e4"+
		"\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5V\3\2\2\2\u00e6\u00e4\3\2\2\2"+
		"\u00e7\u00eb\t\4\2\2\u00e8\u00ea\t\5\2\2\u00e9\u00e8\3\2\2\2\u00ea\u00ed"+
		"\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00f0\3\2\2\2\u00ed"+
		"\u00eb\3\2\2\2\u00ee\u00f0\7\62\2\2\u00ef\u00e7\3\2\2\2\u00ef\u00ee\3"+
		"\2\2\2\u00f0X\3\2\2\2\u00f1\u00f3\t\6\2\2\u00f2\u00f1\3\2\2\2\u00f3\u00f4"+
		"\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6"+
		"\u00f7\b-\2\2\u00f7Z\3\2\2\2\u00f8\u00fa\7\17\2\2\u00f9\u00fb\7\f\2\2"+
		"\u00fa\u00f9\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb\u00fe\3\2\2\2\u00fc\u00fe"+
		"\7\f\2\2\u00fd\u00f8\3\2\2\2\u00fd\u00fc\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff"+
		"\u0100\b.\2\2\u0100\\\3\2\2\2\u0101\u0102\7\61\2\2\u0102\u0103\7,\2\2"+
		"\u0103\u0107\3\2\2\2\u0104\u0106\13\2\2\2\u0105\u0104\3\2\2\2\u0106\u0109"+
		"\3\2\2\2\u0107\u0108\3\2\2\2\u0107\u0105\3\2\2\2\u0108\u010a\3\2\2\2\u0109"+
		"\u0107\3\2\2\2\u010a\u010b\7,\2\2\u010b\u010c\7\61\2\2\u010c\u010d\3\2"+
		"\2\2\u010d\u010e\b/\2\2\u010e^\3\2\2\2\u010f\u0110\7\61\2\2\u0110\u0111"+
		"\7\61\2\2\u0111\u0115\3\2\2\2\u0112\u0114\n\7\2\2\u0113\u0112\3\2\2\2"+
		"\u0114\u0117\3\2\2\2\u0115\u0113\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0118"+
		"\3\2\2\2\u0117\u0115\3\2\2\2\u0118\u0119\b\60\2\2\u0119`\3\2\2\2\13\2"+
		"\u00e4\u00eb\u00ef\u00f4\u00fa\u00fd\u0107\u0115\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}