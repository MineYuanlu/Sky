package bid.yuanlu.sky;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.val;

@SuppressWarnings("javadoc")
@Data
@NoArgsConstructor
public class MusicInfo {
	@Data
	@EqualsAndHashCode(callSuper = true)
	@NoArgsConstructor
	public static final class Music extends MusicInfo {
		ArrayList<Note> songNotes;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	@NoArgsConstructor
	public static final class MusicEncrypted extends MusicInfo {
		byte[]	songNotes;
		int		keyVersion;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static final class Note implements Comparable<Note> {
		long	time;
		String	key;

		/**
		 * 通过音高
		 * 
		 * @param time         表演时间(毫秒)
		 * @param pitch        音高
		 * @param musicalScale 音阶
		 * @return 音符(可能为null, 即代表此乐器无法演奏)
		 */
		public static Note withPitch(long time, int pitch, int musicalScale) {
			System.out.printf("%d %d ", pitch, musicalScale);
			if ((pitch - musicalScale) % 2 != 0) return null;
			val key = (pitch - musicalScale) / 2;
			System.out.printf("%d ", key);
			if (key < 0 || key >= 15) return null;
			return new Note(time, "1Key" + key);
		}

		private static int getKeyNumber(char c) {
			switch (c = Character.toUpperCase(c)) {
			case 'C':
				return 0;
			case 'D':
				return 1;
			case 'E':
				return 2;
			case 'F':
				return 3;
			case 'G':
				return 4;
			case 'A':
				return 5;
			case 'B':
				return 6;
			}
			throw new IllegalArgumentException("Bad Key: " + c);
		}

		public static Note withStepAndOctave(long time, //
				String step, String octave, boolean alter, //
				String baseStep, String baseOctave, boolean baseAlter) {

			int key = (Integer.parseInt(octave) - Integer.parseInt(baseOctave)) * 7//
					+ (getKeyNumber(step.charAt(0)) - getKeyNumber(baseStep.charAt(0)));
			if (baseAlter && !alter) key--;
			if (key < 0 || key >= 15) return null;
			return new Note(time, (alter != baseAlter ? '2' : '1') + "Key" + key);
		}

		@Override
		public int compareTo(Note o) {
			return Long.compare(time, o.time);
		}
	}

	String	name, author, arrangedBy, transcribedBy, permission;
	int		bpm, bitsPerPage, pitchLevel;
}
