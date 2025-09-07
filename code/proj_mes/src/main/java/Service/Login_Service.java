// package Service;
package Service;

import Dao.Login_Dao;
import Dto.Login_Dto;

public class Login_Service {

    private final Login_Dao dao = new Login_Dao();

    public Login_Dto login(String id, String pw) {
        return dao.findByIdAndPw(id, pw);
    }
}
