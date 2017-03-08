package com.darwinsys;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.PolarBlendMode;
import com.kennycason.kumo.PolarWordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;

/**
 * WordCloud makes a word cloud out of two wordlists (ideally one word per line).
 * An example might be one list of topics you know and on of topics you want to learn.k
 * The word lists should be named in the system environment as POSITIVE and NEGATIVE,
 * and each should refer to the full path of the text file, e.g.
 * POSITIVE=~/myfiles/knownwords.txt
 * Use "_" inside the word lists to make phrases appear in the tag cloud.
 * @author Ian Darwin, but most of the work is done by Kenny Cason's kumo package.
 */
public class WordCloud {

    public static void main( String[] args ) throws Exception {
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		//frequencyAnalyzer.setWordFrequenciesToReturn(10);
		frequencyAnalyzer.setMinWordLength(2); // vi
		frequencyAnalyzer.addNormalizer(s -> s.replace('_', ' '));

		final List<WordFrequency> knownWords = 
				frequencyAnalyzer.load(getInputStream("POSITIVE", "/positive.txt"));
		final List<WordFrequency> unknownWords = 
				frequencyAnalyzer.load(getInputStream("NEGATIVE", "/negative.txt"));
		final Dimension dimension = new Dimension(600, 600);
		final PolarWordCloud wordCloud = 
			new PolarWordCloud(dimension, CollisionMode.PIXEL_PERFECT, 
					PolarBlendMode.BLUR);
		wordCloud.setPadding(2);
		wordCloud.setBackground(new CircleBackground(300));
		/** FONT SIZE:
		 * Since in my usage (a glorified TODO list) there is one occurrence of each word,
		 * all words are the same type size, and you have to manually tune the "upper" font size.
		 * In my experience, "10, 15" works for lists of 100 or so words, but it probably
		 * depends on your screen resolution and other system-dependent stuff.
		 */
		wordCloud.setFontScalar(new LinearFontScalar(10, 13));
		wordCloud.build(knownWords, unknownWords);
		wordCloud.writeToFile("wordcloud.png");
    }

	static InputStream getInputStream(String envName, String resourceFileName) throws IOException {
		String absPath = System.getenv(envName);
		if (absPath != null && absPath.startsWith("/")) {
			return new FileInputStream(absPath);
		}
		InputStream is = WordCloud.class.getResourceAsStream(resourceFileName);
		if (is == null) {
			throw new IllegalArgumentException("Can't load resource file " + resourceFileName);
		}
		return is;
	}
}
