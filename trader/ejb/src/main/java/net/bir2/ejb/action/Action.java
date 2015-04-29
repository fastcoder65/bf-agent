package net.bir2.ejb.action;



public enum Action {
		READ_FEEDS("Чтение источников котировок"),
		LOAD_ACTIVE_USERS("Загрузка активных игроков"),
		LOAD_ACTIVE_MARKETS("Загрузка активных рынков"),
		UPDATE_MARKETS("Обновление активных рынков"),
		KEEP_ALIVE("Поддержание сессии betfair"),
		UPDATE_MARKET("Обновление статуса указанного рынка"),    //  обновление статуса и выдача запроса на чтение цен, если статус не "CLOSED"	
		UPDATE_MARKET_PRICES("Обновление цен указанного рынка"); //  чтение цен для юзера
		private String name;
		
		private Action(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
