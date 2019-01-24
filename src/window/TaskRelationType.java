package window;

import java.io.Serializable;

public enum TaskRelationType implements Serializable {
    SS, //Start to Start. Aby zacząć, musi się poprzednie zacząć
    SF, //Start to Finish. Aby zakończyć, musi poprzednie się zacząć.
    FS, //Finish to Start. Aby zacząć, musi poprzednie się zakończyć
    FF; //Finish to Finish, Aby zakończyć, musi poprzednie się zakończyć.
}
