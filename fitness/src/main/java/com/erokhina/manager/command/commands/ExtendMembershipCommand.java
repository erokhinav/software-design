package com.erokhina.manager.command.commands;

import java.sql.Date;

import com.erokhina.common.command.Command;
import com.erokhina.common.command.CommandDao;
import com.erokhina.manager.command.CommandDaoImpl;

public class ExtendMembershipCommand implements Command {
    private Long uid;
    private Date expiryDate;

    public ExtendMembershipCommand(Long uid, Date expiryDate) {
        this.uid = uid;
        this.expiryDate = expiryDate;
    }

    @Override
    public String process(CommandDao commandDao) throws Exception {
        return ((CommandDaoImpl)commandDao).extendSubscription(uid, expiryDate);
    }
}
