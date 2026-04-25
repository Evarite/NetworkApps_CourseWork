package com.hotel.common.enums;

public enum Operation {
    LOGIN,
    REGISTER,

    DISCONNECT,

    UPDATE_ACCOUNT,
    GET_ALL_ACCOUNTS,
    GET_ACCOUNT_BY_ID,
    GET_ACCOUNT_BY_EMAIL,

    GET_ALL_ROOMS,
    GET_AVAILABLE_ROOMS,
    ADD_ROOM,
    UPDATE_ROOM,
    DELETE_ROOM,
    CLOSE_ROOM,
    OPEN_ROOM,

    GET_ALL_GUESTS,
    GET_ALL_GUESTS_WITH_RESERVATIONS,

    CREATE_RESERVATION,
    CANCEL_RESERVATION,
    CHECK_OUT,
    GET_MY_RESERVATIONS,
    GET_ALL_RESERVATIONS,
    APPROVE_RESERVATION,

    GET_ALL_EMPLOYEES,
    HIRE_EMPLOYEE,
    FIRE_EMPLOYEE,
    CHANGE_ROLE,

    GET_MY_RESERVATIONS_AFTER_NOW;

    //Remove later
    @Override
    public String toString() {
        return switch (this) {
            case LOGIN -> "Увайсці ў акаўнт";
            case REGISTER -> "Стварыць новы акаўнт";
            case DISCONNECT -> "Выхад з праграмы";
            case UPDATE_ACCOUNT -> "Рэдагаваць акаўнт";
            case GET_ALL_ACCOUNTS -> "Праглядзець усе акаўнты";
            case GET_ACCOUNT_BY_ID -> "Знайсці акаўнт па ID";
            case GET_ACCOUNT_BY_EMAIL -> "Знайсці акаўнт па email";
            case GET_ALL_ROOMS -> "Праглядзець усе пакоі";
            case GET_AVAILABLE_ROOMS -> "Праглядзець даступныя пакоі";
            case ADD_ROOM -> "Дадаць пакой";
            case UPDATE_ROOM -> "Рэдагаваць пакой";
            case DELETE_ROOM -> "Выдаліць пакой";
            case CLOSE_ROOM -> "Зачыніць пакой на час тэхнічнага абслугоўвання";
            case OPEN_ROOM -> "Адчыніць пакой";
            case GET_ALL_GUESTS -> "Праглядзець усіх гасцей";
            case GET_ALL_GUESTS_WITH_RESERVATIONS -> "Праглядзець усіх гасцей з браніраваннямі";
            case CREATE_RESERVATION -> "Забраніраваць пакой";
            case CANCEL_RESERVATION -> "Скасаваць браніраванне";
            case CHECK_OUT -> "Датэрмінова выселіцца";
            case GET_MY_RESERVATIONS -> "Праглядзець мае браніраванні";
            case GET_ALL_RESERVATIONS -> "Праглядзець усе браніраванні";
            case APPROVE_RESERVATION -> "Усхваліць браніраванне";
            case GET_ALL_EMPLOYEES -> "Праглядзець усіх супрацоўнікаў";
            case HIRE_EMPLOYEE -> "Уладкаваць супрацоўніка";
            case FIRE_EMPLOYEE -> "Зволніць супрацоўніка";
            case CHANGE_ROLE -> "Змяніць пасаду супрацоўніка";
            case GET_MY_RESERVATIONS_AFTER_NOW -> "Праглядзець непачатыя браніраванні";
        };
    }
}
