'use strict';

app.controller('WhiteboardSidebarController', ['$scope', 'WhiteboardSocketService',
function($scope, whiteboardSocketService){
    function Collaborator(id, name, join, online, owner) {
        this.id = id;
        this.name = name;
        //TODO: online != join. online -> logged-in in App. join -> logged-in Whiteboard.
        this.online = (typeof online != 'undefined') ? online : false;
        this.join = (typeof join != 'undefined') ? join : false;
        //TODO: Adding owner flag for special STAR ;-)
        this.owner = (typeof owner != 'undefined') ? owner : false;
        this.toString = function() {return "{" + this.id + "," + this.name + "}";}
    }
    function WhiteboardLog(id, name, typ, logDate){
        this.id = id;
        this.name = name;
        this.typ = typ;
        this.logDate = new Date(logDate);
        this.dateForHtml = this.logDate.getHours() + ":" + this.logDate.getMinutes() + ":" + this.logDate.getSeconds();
        this.typForHtml = "";
        switch (this.typ){
            case "FreeHandEvent":
                this.typForHtml = "freihand";
                break;
            case "LineEvent":
                this.typForHtml = "eine Linie";
                break;
        }
        this.forHTML = "[" + this.dateForHtml + "] " + this.name + " zeichnete " + this.typForHtml;
    }

    $scope.collaborators = [];
    $scope.whiteboardlog = [];

    whiteboardSocketService.registerForSocketEvent('DrawFinishEvent', function(drawEvent){
        $scope.$apply(function(){
            $scope.whiteboardlog.unshift(new WhiteboardLog(drawEvent.boardElementId, drawEvent.user.username, drawEvent.drawType, drawEvent.logDate));
        });
    });

    whiteboardSocketService.registerForSocketEvent('BoardUserOpenEvent', function(boardUserOpenEvent) {
        //-> user joining...
        $scope.$apply(function() {
            //need scope-apply cause we are out of angular digest cycle, when the server sends events and calls this callback
            var alreadyMember = false;
            $scope.collaborators.forEach(function(collab) {
                if (collab.id === boardUserOpenEvent.user.userId) {
                    collab.join = true;
                    alreadyMember = true;
                }
            });
            if (!alreadyMember) {
                $scope.collaborators.push(new Collaborator(boardUserOpenEvent.user.userId, boardUserOpenEvent.user.username, true, true, true));
            }
        });
    });
    whiteboardSocketService.registerForSocketEvent('InitialBoardStateEvent', function(initStateEvent) {
        $scope.$apply(function() {
            initStateEvent.colaborators.forEach(function (collab) {
                $scope.collaborators.push(new Collaborator(collab.user.userId, collab.user.username, collab.joined, true, true));
            });
        });
    });
    whiteboardSocketService.registerForSocketEvent('BoardUserCloseEvent', function(boardUserCloseEvent) {
        $scope.$apply(function() {
            $scope.collaborators.forEach(function(collab) {
                if (collab.id === boardUserCloseEvent.user.userId) {
                    collab.join = false;
                }
            });
        });
    });
}]);