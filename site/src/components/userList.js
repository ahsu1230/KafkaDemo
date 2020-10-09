import React from "react";
import "./userList.sass";
import moment from "moment";
import SelectLevels from "./selectLevels.js";
import { USER_NORMAL, USER_QUEUED, USER_MATCHED } from "../userStates.js";

class UserList extends React.Component {
    onChangeLevel = (e, user) => {
        user.level = parseInt(e.target.value);
        this.props.onUpdateUser(user);
    };

    onChangeOnlineStatus = (user, isOnline) => {
        user.isOnline = isOnline;
        this.props.onUpdateUser(user);
    };

    onQueueUser = (user) => {
        user.state = USER_QUEUED;
        user.stateChangedAt = moment();
        this.props.onUpdateUser(user);
    };

    onDequeueUser = (user) => {
        user.state = USER_NORMAL;
        user.stateChangedAt = moment();
        this.props.onUpdateUser(user);
    };

    render = () => {
        const users = this.props.users || [];
        const items = users.map((user, index) => {
            return (
                <li className="user-row" key={index}>
                    <div>
                        <div
                            className={
                                "dot-online" +
                                (!user.isOnline ? " inactive" : "")
                            }></div>
                        <span className="id">{user.id}</span>
                        <span className="username">{user.username}</span>
                        <span
                            className={
                                "online-status" +
                                (user.isOnline ? "" : " inactive")
                            }>
                            {user.isOnline ? "Online" : "Offline"}
                        </span>
                        <SelectLevels
                            value={user.level}
                            onSelect={(e) => this.onChangeLevel(e, user)}
                        />
                    </div>
                    <div>
                        {user.isOnline && (
                            <UserStateButton
                                user={user}
                                onQueue={this.onQueueUser}
                                onDequeue={this.onDequeueUser}
                            />
                        )}
                        {user.isOnline ? (
                            <button
                                className="offline"
                                onClick={() =>
                                    this.onChangeOnlineStatus(user, false)
                                }>
                                Go Offline
                            </button>
                        ) : (
                            <button
                                className="online"
                                onClick={() =>
                                    this.onChangeOnlineStatus(user, true)
                                }>
                                Go Online
                            </button>
                        )}
                    </div>
                </li>
            );
        });

        return (
            <section id="user-list">
                <h2>Users</h2>
                <ul>{items}</ul>
            </section>
        );
    };
}

class UserStateButton extends React.Component {
    render() {
        const user = this.props.user;

        return (
            <div className="state">
                {user.state == USER_NORMAL && (
                    <button
                        className="action-queue"
                        onClick={() => this.props.onQueue(user)}>
                        Queue
                    </button>
                )}
                {user.state == USER_QUEUED && (
                    <span className="state-queued">
                        <span>
                            Queued {user.stateChangedAt.fromNow() + "..."}
                        </span>
                        <button
                            className="action-dequeue"
                            onClick={() => this.props.onDequeue(user)}>
                            Dequeue
                        </button>
                    </span>
                )}
                {user.state == USER_MATCHED && (
                    <span className="state-matched">
                        Started match {user.stateChangedAt.fromNow()}
                    </span>
                )}
            </div>
        );
    }
}

export default UserList;
