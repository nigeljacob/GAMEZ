import android.os.Parcel
import android.os.Parcelable

data class AnswerOptionLayout(
    val isSelected: Boolean,
    val title: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AnswerOptionLayout> {
        override fun createFromParcel(parcel: Parcel): AnswerOptionLayout {
            return AnswerOptionLayout(parcel)
        }

        override fun newArray(size: Int): Array<AnswerOptionLayout?> {
            return arrayOfNulls(size)
        }
    }
}
