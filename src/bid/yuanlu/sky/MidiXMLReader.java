package bid.yuanlu.sky;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.google.gson.Gson;

import bid.yuanlu.sky.MusicInfo.Music;
import bid.yuanlu.sky.MusicInfo.Note;
import lombok.val;

/**
 * MIDI/XML乐谱读取
 * 
 * @author yuanlu
 *
 */
public class MidiXMLReader {
	@SuppressWarnings("javadoc")
	public static void main(String[] args) throws Throwable {
		new MidiXMLReader().readXML("错位时空");
	}

	/**
	 * XML读取
	 * 
	 * @param name 文件名
	 * @throws Exception 运行错误
	 */
	public void readXML(String name) throws Exception {
		val		doc			= Jsoup.parse(new File("sky\\xml\\" + name + ".xml"), "UTF-8");
		val		divisions	= Integer.parseInt(doc.selectFirst("attributes divisions").ownText());
		for (val part : doc.select("part")) {
			buildMuisc(name + "-" + part.attr("id"), divisions, readNotes(divisions, part));
		}
	}

	/**
	 * 构建一条音轨
	 * 
	 * @param name      文件名
	 * @param divisions bpm
	 * @param songNotes 音符
	 */
	private void buildMuisc(String name, int divisions, ArrayList<Note> songNotes) {
		Music music = new Music();
		music.songNotes	= new ArrayList<>();
		music.bpm		= divisions;
		music.name		= name;
		music.author	= "YUANLU MIDI XML";
		music.songNotes	= songNotes;
		music.songNotes.sort(null);
		try (val out = new FileWriter("sky\\play\\" + name + ".txt")) {
			new Gson().toJson(new Music[] { music }, out);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 翻译XML
	 * 
	 * @param bpm  bpm
	 * @param part 音轨
	 * @return 音轨音符
	 */
	private ArrayList<Note> readNotes(long bpm, Element part) {
		val				baseStep	= part.selectFirst("attributes clef sign").ownText();
		val				baseOctave	= part.selectFirst("attributes clef line").ownText();
		ArrayList<Note>	notes	= new ArrayList<>();
		long			time	= 0;
//		System.out.println(part.select("note").size());
		for (val noteData : part.select("note")) {
			for (val pitch : noteData.select("pitch")) {
				val note = Note.withStepAndOctave(time, //
						pitch.selectFirst("step").ownText(), pitch.selectFirst("octave").ownText(), pitch.selectFirst("alter") != null, //
						baseStep, baseOctave, false);
				if (note != null) notes.add(note);
			}
			long tick = Long.parseLong(noteData.selectFirst("duration").ownText());
			time += tick * 1000 / bpm;
		}
		return notes;
	}
}
