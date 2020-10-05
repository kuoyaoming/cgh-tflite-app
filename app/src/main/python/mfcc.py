from java import jclass, jarray, jdouble
import librosa
import numpy as np


def mfcc(file_path):
    y, _ = librosa.load(file_path, mono=True, sr=None)
    D = np.abs(librosa.stft(y, n_fft=512))
    p = librosa.amplitude_to_db(D, ref=np.max)
    tmp = np.zeros([257, 500])
    if p.shape[1] < 500:
        tmp[:257, :p.shape[1]] = p
    else:
        tmp[:257, :500] = p[:257, :500]
    tmp = (((tmp*-1)/50)-1)
    tmp = tmp.astype('float32')
    JavaBean = jclass("com.cgh.org.audio.TFlite.JavaBean")
    jb = JavaBean()
    jb.setData(jarray(jarray(jdouble))(tmp))
    return jb
