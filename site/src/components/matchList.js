import React from "react";
import { Transition } from "react-transition-group";
import "./matchList.sass";

class MatchList extends React.Component {
    render() {
        const matches = this.props.matches || [];
        const items = matches.map((match, index) => (
            <MatchRow
                key={index}
                in={true}
                match={match}
                onEndMatch={this.props.onEndMatch}
            />
        ));

        return (
            <section id="match-list">
                <h2>Matches</h2>
                <ul>{items}</ul>
            </section>
        );
    }
}

const defaultStyle = {
    position: "relative",
    right: 0,
    opacity: 0.0,
    visibility: "hidden",
    transition: "0.1s linear all",
};

const transitionStyles = {
    entering: { opacity: 1.0, visibility: "visible", right: "0px" },
    entered: { opacity: 1.0, visibility: "visible", right: "0px" },
    exiting: { opacity: 0.0, visibility: "hidden", right: "-40px" },
    exited: { opacity: 0.0, visibility: "hidden", right: "-40px" },
};

class MatchRow extends React.Component {
    state = {
        winnerId: this.props.match.users[0].id,
    };

    onChangeWinner = (winnerId) => {
        this.setState({ winnerId: winnerId });
    };

    onEndMatch = () => {
        let match = this.props.match;
        match.winnerId = this.state.winnerId;
        console.log("Match ended!");
        this.props.onEndMatch(match);
    };

    render() {
        const match = this.props.match;
        const userItems = (match.users || []).map((user, index) => (
            <div className="user-row" key={index}>
                <span>userId: {user.id}</span>
                <span>{user.username}</span>
                <span>Level {user.level}</span>
                <div className="winner">
                    <input
                        type="radio"
                        onChange={() => this.onChangeWinner(user.id)}
                        checked={this.state.winnerId == user.id}
                    />
                    <span>Winner</span>
                </div>
            </div>
        ));
        return (
            <Transition in={this.props.in} timeout={400}>
                {(state) => (
                    <li
                        className="match-row"
                        style={{
                            ...defaultStyle,
                            ...transitionStyles[state],
                        }}>
                        <div className="header">
                            <div>
                                <h3>Match Id: {match.id}</h3>
                                <h4>
                                    Match started {match.startedAt.fromNow()}
                                </h4>
                            </div>
                            <button onClick={this.onEndMatch}>End Match</button>
                        </div>

                        <div>{userItems}</div>
                        <span>{match.time}</span>
                    </li>
                )}
            </Transition>
        );
    }
}

export default MatchList;
