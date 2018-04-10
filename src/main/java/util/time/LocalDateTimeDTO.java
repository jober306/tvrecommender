package util.time;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LocalDateTimeDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	final int year;
	final int month;
	final int dayOfMonth;
	
	final int hour;
	final int minute;
	final int second;
	final int nano;
	
	public  LocalDateTimeDTO(LocalDateTime dateTime){
		this.year = dateTime.getYear();
		this.month = dateTime.getMonthValue();
		this.dayOfMonth = dateTime.getDayOfMonth();
		this.hour = dateTime.getHour();
		this.minute = dateTime.getMinute();
		this.second = dateTime.getSecond();
		this.nano = dateTime.getNano();
	}
	
	public LocalDateTime toLocalDateTime(){
		return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nano);
	}
	
	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}

	public int getNano() {
		return nano;
	}

    public static int compare(LocalDateTimeDTO dto, LocalDateTimeDTO otherDto){
        int cmp = (dto.getYear() - otherDto.getYear());
        if (cmp == 0) {
            cmp = (dto.getMonth() - otherDto.getMonth());
            if (cmp == 0) {
                cmp = (dto.getDayOfMonth() - otherDto.getDayOfMonth());
                if(cmp == 0){
                    cmp = Integer.compare(dto.getHour(), otherDto.getHour());
                    if (cmp == 0) {
                        cmp = Integer.compare(dto.getMinute(), otherDto.getMinute());
                        if (cmp == 0) {
                            cmp = Integer.compare(dto.getSecond(), otherDto.getSecond());
                            if (cmp == 0) {
                                cmp = Integer.compare(dto.getNano(), otherDto.getNano());
                            }
                        }
                    }
                }
            }
        }
        return cmp;
    }
	
	public static LocalDateTimeDTO min(LocalDateTimeDTO dto, LocalDateTimeDTO otherDto){
		int cmp = compare(dto, otherDto);
		if(cmp < 0){
			return dto;
		}else{
			return otherDto;
		}
	}
	
	public static LocalDateTimeDTO max(LocalDateTimeDTO dto, LocalDateTimeDTO otherDto){
		int cmp = compare(dto, otherDto);
		if(cmp > 0){
			return dto;
		}else{
			return otherDto;
		}
	}
}
