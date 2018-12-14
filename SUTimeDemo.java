package com.gs.fast.eq.bot.services;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class SUTimeDemo {

    /**
     * Example usage:
     * java SUTimeDemo "Three interesting dates are 18 Feb 1997, the 20th of july and 4 days from today."
     *
     * @param args Strings to interpret
     */
    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("sutime.rules", "src/main/resources/config/fastbot.sutime.txt,edu/stanford/nlp/models/sutime/defs.sutime.txt,edu/stanford/nlp/models/sutime/english.sutime.txt,edu/stanford/nlp/models/sutime/english.holidays.sutime.txt");
        props.setProperty("sutime.markTimeRanges", "true");
        props.setProperty("sutime.includeRange", "true");

        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        pipeline.addAnnotator(new TimeAnnotator("sutime", props));

//        String text = "Three interesting dates are 18 Feb 1997, the 20th of july and 4 days from today, 19890313, since 2018-02-09 .";
//        String text = "What is the rfq hit ratio for Japan year to date. ";
//        String text = "What is the rfq hit ratio for Japan YTD? where were you in the last twenty four hours? ";
//        String text = "He like to eat food from 4 days from today to today. Where were you last week? more than two thousand years. ";
//        String text = "From next month, we will have meeting on every friday, from 3:00 pm to 4:00 pm";
        String text = "we have no more drinks since the first day of the year. What is the rfq hit ratio for Japan year to date? ";

        Annotation annotation = new Annotation(text);
        annotation.set(CoreAnnotations.DocDateAnnotation.class, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        pipeline.annotate(annotation);
        System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
        List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);

        for (CoreMap cm : timexAnnsAll) {
            List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
            System.out.println(cm + " [from char offset " +
                tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
                " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
                " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());
            TimeExpression te = cm.get(TimeExpression.Annotation.class);
            SUTime.Temporal temporal = te.getTemporal();
            System.out.println(temporal);

        }
    }

}
