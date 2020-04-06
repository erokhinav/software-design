package com.erokhina.manager.command.commands;

import com.erokhina.common.command.Command;
import com.erokhina.common.command.CommandDao;
import com.erokhina.manager.command.CommandDaoImpl;

public class AddNewUserCommand implements Command {
    public String process(CommandDao commandDao) throws Exception {
        return ((CommandDaoImpl) commandDao).addNewUser();
    }
}
